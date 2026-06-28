package com.mgs.savedData;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record GraveInventoryData(
        String stackNBT,
        @Nullable String slotType,
        int slot
) {
}

