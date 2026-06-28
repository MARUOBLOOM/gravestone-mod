package com.mgs.savedData;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record GraveData(
        UUID ownerId,
        List<GraveInventoryData> inventory,
        int deathXp,
        int blockX,
        int blockY,
        int blockZ,
        String dimension,
        Date deathDate,
        @Nullable String deathReason
) {
}
