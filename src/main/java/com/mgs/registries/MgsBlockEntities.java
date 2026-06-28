package com.mgs.registries;

import com.mgs.GraveStoneBlockEntity;
import com.mgs.Mgs;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MgsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Mgs.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraveStoneBlockEntity>> GRAVESTONE_BLOCK;

    static {
        GRAVESTONE_BLOCK = BLOCK_ENTITIES.register("gravestone", () -> BlockEntityType.Builder.of(GraveStoneBlockEntity::new, new Block[]{(Block) MgsBlocks.GRAVE_STONE.get()}).build((Type) null));
    }
}
