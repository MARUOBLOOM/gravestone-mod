package com.mgs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class TestInventoryScreen extends Screen {
    public TestInventoryScreen() {
        super(Component.literal("Inventory Preview"));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int left = width / 2 - 90;
        int top = height / 2 - 40;

        drawInventory(guiGraphics, left, top);
    }

    private void drawInventory(GuiGraphics gui, int left, int top) {

        // メインインベントリ
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {

                drawSlot(gui,
                        left + col * 18,
                        top + row * 18,
                        new ItemStack(Items.DIAMOND));
            }
        }

        // ホットバー
        for (int col = 0; col < 9; col++) {

            drawSlot(gui,
                    left + col * 18,
                    top + 58,
                    new ItemStack(Items.DIAMOND_SWORD));
        }
    }

    private void drawSlot(GuiGraphics gui, int x, int y, ItemStack stack) {

        gui.fill(x, y, x + 18, y + 18, 0xFF8B8B8B);
        gui.fill(x + 1, y + 1, x + 17, y + 17, 0xFF373737);

        gui.renderItem(stack, x + 1, y + 1);
        gui.renderItemDecorations(
                Minecraft.getInstance().font,
                stack,
                x + 1,
                y + 1
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
