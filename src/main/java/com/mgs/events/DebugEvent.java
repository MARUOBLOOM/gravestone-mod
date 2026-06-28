package com.mgs.events;

import com.mgs.Mgs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = Mgs.MODID)
public class DebugEvent {
    @SubscribeEvent
    public static void forDebug(PlayerInteractEvent.RightClickBlock ev) {
        Player player = ev.getEntity();
        Level level = ev.getLevel();

        // *Curiosのスロットにアイテムをセットするコード*
        if (level.getBlockState(ev.getPos()).is(Blocks.DIRT)) {
            setItemToCuriosSlot(player, "hands", 0, player.getItemInHand(InteractionHand.MAIN_HAND));
        }
    }

    private static void setItemToCuriosSlot(Player player, String identifier, int slot, ItemStack stack) {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            var stacksHandler = curios.getCurios().get(identifier);
            // 存在しないスロットカテゴリであれば終了
            if (stacksHandler == null) {
                sendToInvOrDrop(player, stack);
                return;
            }

            var handler = stacksHandler.getStacks();
            // 該当スロットが空であればアイテムをセットする
            if (handler.getStackInSlot(slot).isEmpty()) {
                handler.setStackInSlot(slot, stack.copy());
                return;
            }
            // すでにスロットにアイテムがあればバニラインベントリに移すor地面にドロップ
            sendToInvOrDrop(player, stack);
        });
    }

    private static void sendToInvOrDrop(Player player, ItemStack stack) {
        // バニラインベントリに移せるか実行
        boolean canAdd = player.getInventory().add(stack.copy());
        // インベントリに移せなかった場合は地面にドロップする
        if (!canAdd) player.drop(stack.copy(), false);
    }
}
