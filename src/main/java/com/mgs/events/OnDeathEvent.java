package com.mgs.events;

import com.mgs.GraveStoneBlockEntity;
import com.mgs.Mgs;
import com.mgs.registries.MgsBlocks;
import com.mgs.savedData.GraveData;
import com.mgs.savedData.GraveDataManager;
import com.mgs.savedData.GraveInventoryData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@EventBusSubscriber(modid = Mgs.MODID)
public class OnDeathEvent {
    @SubscribeEvent
    public static void PlayerOnDeath(LivingDeathEvent ev) {
        // 死んだEntityがPlayer型でなければスキップ
        if (!(ev.getEntity() instanceof ServerPlayer deadPlayer)) return;
        // Playerのインベントリ情報を取得 ※中身がnullであれば処理しない
        Inventory playerInv = deadPlayer.getInventory();
        if (playerInv.isEmpty()) return;

        // 墓のUUIDを生成、プレイヤーUUIDを取得
        UUID graveId = UUID.randomUUID();
        UUID playerUUID = deadPlayer.getUUID();
        String playerName = deadPlayer.getGameProfile().getName();

        Mgs.LOGGER.debug("Profile={}", deadPlayer.getGameProfile());
        Mgs.LOGGER.debug("Name={}", deadPlayer.getGameProfile().getName());
        Mgs.LOGGER.debug("DisplayName={}", deadPlayer.getName().getString());

        // インベントリ情報保存用配列
        List<GraveInventoryData> savedInv = new ArrayList<>();

        // バニラインベントリ情報を保存
        IntStream.range(0, playerInv.getContainerSize()).forEach(slot -> {
            ItemStack stack = playerInv.getItem(slot);
            // ItemStackがEmpty(空)判定でなければスロット番号と保存する
            if (!stack.isEmpty()) {
                savedInv.add(new GraveInventoryData(itemStackToNBT(stack, deadPlayer.registryAccess()), null, slot));
            }
        });

        //Curiosインベントリ情報を保存
        CuriosApi.getCuriosInventory(deadPlayer).ifPresent(curios -> {
            curios.getCurios().forEach((identifier, stacksHandler) -> {
                var handler = stacksHandler.getStacks();
                IntStream.range(0, handler.getSlots()).forEach(curiosSlot -> {
                    ItemStack stack = handler.getStackInSlot(curiosSlot);
                    // ItemStackがEmpty(空)判定でなければスロット番号と保存する
                    if (!stack.isEmpty()) {
                        savedInv.add(new GraveInventoryData(itemStackToNBT(stack, deadPlayer.registryAccess()), identifier, curiosSlot));
                    }
                });
            });
        });

        // 死亡情報一覧の生成
        GraveData deadData = new GraveData(
                playerUUID,
                savedInv,
                deadPlayer.totalExperience,
                deadPlayer.blockPosition().getX(),
                deadPlayer.blockPosition().getY(),
                deadPlayer.blockPosition().getZ(),
                deadPlayer.level().dimension().toString(),
                new Date(),
                ev.getSource().getLocalizedDeathMessage(deadPlayer).getString()
        );

        // 死亡地点に墓石生成
        deadPlayer.level().setBlock(deadPlayer.blockPosition(), MgsBlocks.GRAVE_STONE.get().defaultBlockState(), Block.UPDATE_ALL);
        // 死亡地点が空中かつ墓石の真下が空気であればブロック生成
        if (deadPlayer.level().getBlockState(deadPlayer.blockPosition().below()).is(Blocks.AIR)) {
            deadPlayer.level().setBlock(deadPlayer.blockPosition().below(), Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
        }

        // 墓石のエンティティに墓石ID、オーナーUUID、オーナー名を保存
        BlockEntity be = deadPlayer.level().getBlockEntity(deadPlayer.blockPosition());
        if (be instanceof GraveStoneBlockEntity grave) {
            grave.setGraveId(graveId);
            grave.setOwnerId(playerUUID);
            grave.setOwnerName(playerName);
            try {
                // 大元のフォルダがあるかチェック
                Path gameDir = FMLPaths.GAMEDIR.get().resolve("mgsGraveDatas");
                ensureStorageDir(gameDir);
                // さらに該当プレイヤーのフォルダがあるかチェック
                Path playerDir = gameDir.resolve(playerUUID.toString());
                ensurePlayerDir(playerDir);
                // 親フォルダがあれば死亡情報をまとめたJsonを生成する
                GraveDataManager.createGraveJsonData(playerDir.resolve(graveId.toString() + ".json"), deadData);
            } catch (Exception e) {
                Mgs.LOGGER.error("file creation was failed." + e);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent ev) {
        // プレイヤーが死んだ際にしか発火しないよう制御
        if (!(ev.getEntity() instanceof ServerPlayer)) return;
        ev.getDrops().clear();
    }

    private static void ensureStorageDir(Path targetDir) throws IOException {
        if (Files.notExists(targetDir)) {
            // 指定フォルダがなければ作成する
            Files.createDirectories(targetDir);
        }
    }

    private static void ensurePlayerDir(Path targetDir) throws IOException {
        if (Files.notExists(targetDir)) {
            // 指定フォルダがなければ作成する
            Files.createDirectories(targetDir);
        }
    }

    public static String itemStackToNBT(ItemStack stack, HolderLookup.Provider provider) {
        CompoundTag tag = (CompoundTag) stack.saveOptional(provider);
        return tag.toString();
    }
}
