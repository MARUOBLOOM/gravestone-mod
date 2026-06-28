package com.mgs.registries;

import com.mgs.GraveStoneBlock;
import com.mgs.Mgs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MgsBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Mgs.MODID);
    public static final DeferredBlock<Block> GRAVE_STONE;

    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> properties) {
        DeferredBlock<T> block = BLOCKS.register(name, properties);
        MgsItems.ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    static {
        GRAVE_STONE = register("gravestone", () -> new GraveStoneBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion()));
    }
}
