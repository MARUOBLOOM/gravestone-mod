package com.mgs.events;

import com.mgs.GraveStoneBlockEntity;
import com.mgs.Mgs;
import com.mgs.savedData.GraveData;
import com.mgs.savedData.GraveDataManager;
import com.mgs.savedData.GraveInventoryData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Mgs.MODID)
public class OnInteractEvent {
    @SubscribeEvent
    public static void OnInteractGraveStone(PlayerInteractEvent.RightClickBlock ev) {
        Player player = ev.getEntity();
        Level level = ev.getLevel();
        // メインハンドでの右クリかつサーバサイド以外の処理は拾わない
        if (ev.getLevel().isClientSide()) return;
        if (ev.getHand() != InteractionHand.MAIN_HAND) return;

        // Blockを右クリしたエンティティがPlayer型でなければスキップ
        if (!(player instanceof ServerPlayer clickPlayer)) return;
        // 復元処理の実行
        restoreInventory(level, clickPlayer, ev.getPos());
    }

    public static void restoreInventory(Level level, Player deadPlayer, BlockPos deadPos) {
        // 右クリした対象のブロックエンティティがGraveStoneBlockEntityでなければスキップ
        if (level.getBlockEntity(deadPos) instanceof GraveStoneBlockEntity grave) {
            // 墓石ID、オーナーIDを取得
            UUID graveId = grave.getGraveId();
            UUID ownerId = grave.getOwnerId();

            // 墓石ID、オーナーIDが空白であればスキップ
            if (graveId == null || ownerId == null) {
                deadPlayer.displayClientMessage(Component.translatable("message.mgs.gravestone.missingproperty").withStyle(ChatFormatting.RED), false);
                return;
            }
            // 右クリしたプレイヤーUUIDとブロックに紐づいたUUIDが一致しない場合は処理対象外
            if (!deadPlayer.getUUID().equals(ownerId)) {
                deadPlayer.displayClientMessage(Component.translatable("message.mgs.gravestone.missingowner").withStyle(ChatFormatting.RED), false);
                return;
            }

            try {
                // 大元のフォルダがあるかチェック
                Path jsonPath = FMLPaths.GAMEDIR.get().resolve("mgsGraveDatas").resolve(ownerId.toString()).resolve(graveId.toString() + ".json");
                // 墓石IDと一致するJsonがあるか判定
                if (Files.notExists(jsonPath)) {
                    deadPlayer.displayClientMessage(Component.translatable("message.mgs.gravestone.missinginventory").withStyle(ChatFormatting.RED), false);
                    return;
                }
                ;

                // Jsonファイルがあれば読み込む
                GraveData savedGraveData = GraveDataManager.readGraveJsonData(jsonPath);
                // インベントリセットに失敗した情報を保管する配列
                List<GraveInventoryData> pendingItems = new ArrayList<>();

                Mgs.LOGGER.debug(grave.getOwnerName());

                // アイテム情報を格納したArrayをforEachで回す
                savedGraveData.inventory().forEach(item -> {
                    // NBT化したアイテムをItemStackに変換する
                    try {
                        ItemStack stack = nbtToItemStack(item.stackNBT(), deadPlayer.registryAccess());
                        // slotTypeがnullかどうかでバニラかCuriosスロットに入れるか判定
                        if (item.slotType() == null) {
                            boolean canSet = setItemToSlot(deadPlayer, stack, item.slot());
                            // もしスロットが存在しないなどでセットできなければ失敗用配列へ格納
                            if (!canSet) {
                                pendingItems.add(item);
                            }
                        } else {
                            boolean canSet = setItemToCuriosSlot(deadPlayer, item.slotType(), item.slot(), stack);
                            // もしスロットが存在しないなどでセットできなければ失敗用配列へ格納
                            if (!canSet) {
                                pendingItems.add(item);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    deadPlayer.getInventory().setChanged();
                });

                // セットに失敗したアイテムのリトライ
                pendingItems.forEach(failedItem -> {
                    try {
                        ItemStack stack = nbtToItemStack(failedItem.stackNBT(), deadPlayer.registryAccess());
                        // slotTypeがnullかどうかでバニラかCuriosスロットに入れるか判定
                        if (failedItem.slotType() == null) {
                            // インベントリorドロップするようにする
                            sendToInvOrDrop(deadPlayer, stack);
                        } else {
                            boolean canSet = setItemToCuriosSlot(deadPlayer, failedItem.slotType(), failedItem.slot(), stack);
                            // リトライ時でもセットできなければバニラインベントリorドロップさせる
                            if (!canSet) {
                                sendToInvOrDrop(deadPlayer, stack);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    deadPlayer.getInventory().setChanged();
                });
                // Jsonファイルを削除する
                deleteInventoryJson(jsonPath);
                // 墓石ブロックを消す
                level.setBlock(deadPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            } catch (Exception e) {
                Mgs.LOGGER.error("file read was failed." + e);
            }
        }
    }

    private static void deleteInventoryJson(Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            // Jsonファイルの削除
            Files.delete(targetDir);
        }
    }

    public static ItemStack nbtToItemStack(String nbtString, HolderLookup.Provider provider) throws CommandSyntaxException {
        CompoundTag compoundTag = TagParser.parseTag(nbtString);
        return ItemStack.parseOptional(provider, compoundTag);
    }

    private static boolean setItemToSlot(Player player, ItemStack stack, int slot) {
        Inventory inventory = player.getInventory();
        // 指定スロットが範囲外であればfalse
        if (slot < 0 || slot >= inventory.getContainerSize()) return false;
        // すでに指定スロットにアイテムがあればfalse
        if (!inventory.getItem(slot).isEmpty()) return false;
        // アイテムをセット
        inventory.setItem(slot, stack.copy());
        return true;
    }

    private static boolean setItemToCuriosSlot(Player player, String identifier, int slot, ItemStack stack) {
        try {
            // Curiosインベントリを取得
            var curiosOpt = CuriosApi.getCuriosInventory(player);
            if (curiosOpt.isEmpty()) return false;

            // 存在しないスロットカテゴリであれば終了
            var stacksHandler = curiosOpt.get().getCurios().get(identifier);
            if (stacksHandler == null) return false;

            var handler = stacksHandler.getStacks();
            // 該当スロット番号があるか判定
            if (slot < 0 || slot >= handler.getSlots()) return false;
            // 該当スロットが空かどうか判定
            if (!handler.getStackInSlot(slot).isEmpty()) return false;
            // 空であればスロットにセット
            handler.setStackInSlot(slot, stack.copy());
            return true;
        } catch (Exception e) {
            Mgs.LOGGER.error("Failed to equip curios item." + e);
            return false;
        }
    }

    private static void sendToInvOrDrop(Player player, ItemStack stack) {
        ItemStack remaining = stack.copy();
        // バニラインベントリに移せるか実行(空きスロットがない場合はドロップ)
        if (player.getInventory().getFreeSlot() == -1){
            player.drop(remaining, false);
        }else{
            player.getInventory().add(remaining);
        }
    }
}
