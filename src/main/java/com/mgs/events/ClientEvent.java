package com.mgs.events;

//import com.mgs.GraveStoneBlockEntityRenderer;
import com.mgs.Mgs;
import com.mgs.gui.TestInventoryScreen;
import com.mgs.registries.MgsBlockEntities;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = Mgs.MODID, value = Dist.CLIENT)
public class ClientEvent {
//    @SubscribeEvent
//    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(MgsBlockEntities.GRAVESTONE_BLOCK.get(), GraveStoneBlockEntityRenderer::new);
//    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent ev) {
        CommandDispatcher<CommandSourceStack> dispatcher = ev.getDispatcher();

//        dispatcher.register(Commands.literal("testinv").executes(context -> {
//            Minecraft.getInstance().setScreen(new TestInventoryScreen());
//            return 1;
//        }));

    }
}
