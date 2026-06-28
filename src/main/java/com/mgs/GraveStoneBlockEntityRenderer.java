//package com.mgs;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.ItemDisplayContext;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import org.jetbrains.annotations.NotNull;
//import org.joml.Matrix4f;
//
//import java.util.Objects;
//import java.util.UUID;
//
//public class GraveStoneBlockEntityRenderer implements BlockEntityRenderer<GraveStoneBlockEntity> {
//
//    public GraveStoneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
//    }
//
//    @Override
//    public void render(@NotNull GraveStoneBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
//        String ownerName = blockEntity.getOwnerName();
//        UUID ownerId = blockEntity.getOwnerId();
//
//        LocalPlayer deadPlayer = Minecraft.getInstance().player;
//        // 墓石のOwnerUUIDと一致しないプレイヤーであれば描画しない
//        if (deadPlayer != null && !deadPlayer.getUUID().equals(ownerId)) return;
//        Font font = Minecraft.getInstance().font;
//        // 墓石にOwnerNameが設定されていなければ描画しない
//        if (ownerName == null) return;
//        String text = Component.literal(blockEntity.getOwnerName()).append(Component.translatable("render.gravestone.nametag")).getString();
//
//        poseStack.pushPose();
//        poseStack.translate(0.5D, 1.3D, 0.5D);
//        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//
//        poseStack.scale(0.035F, -0.035F, 0.035F);
//        Matrix4f matrix = poseStack.last().pose();
//        float x = -font.width(text) / 2.0F;
//        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
//        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
//
//        // 背景付き文字
//        font.drawInBatch(text, x, 0, 0x20FFFFFF, false, matrix, buffer, Font.DisplayMode.SEE_THROUGH, backgroundColor, 15728880);
//        // 本体文字
//        font.drawInBatch(text, x, 0, 0xFFFFFFFF, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
//        poseStack.popPose();
//    }
//
//    @Override
//    public boolean shouldRenderOffScreen(@NotNull GraveStoneBlockEntity blockEntity) {
//        return true;
//    }
//}
