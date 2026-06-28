package com.mgs.registries;

import com.mgs.Mgs;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class MgsDataComponents {
    public static final DeferredRegister.DataComponents DC = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Mgs.MODID);
}
