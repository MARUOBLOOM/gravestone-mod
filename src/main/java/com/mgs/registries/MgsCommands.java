package com.mgs.registries;

import com.mgs.Mgs;
import com.mgs.gui.TestInventoryScreen;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Mgs.MODID)
public class MgsCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent ev) {
        CommandDispatcher<CommandSourceStack> dispatcher = ev.getDispatcher();

//        dispatcher.register(Commands.literal("grave")
//                .then(Commands.argument("target", EntityArgument.player())
//                        .executes(ctx -> {
//                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
//                            Mgs.LOGGER.debug(target.toString());
//                            return 1;
//                        })));
    }
}
