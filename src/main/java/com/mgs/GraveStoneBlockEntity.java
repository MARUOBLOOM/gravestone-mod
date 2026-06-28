package com.mgs;

import com.mgs.registries.MgsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GraveStoneBlockEntity extends BlockEntity {
    private UUID graveId;
    private UUID ownerId;
    private String ownerName;

    public GraveStoneBlockEntity(BlockPos pos, BlockState state) {
        super(MgsBlockEntities.GRAVESTONE_BLOCK.get(), pos, state);
    }

    public UUID getGraveId() {
        return this.graveId;
    }

    public void setGraveId(UUID graveUUID) {
        this.graveId = graveUUID;
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(UUID playerUUID) {
        this.ownerId = playerUUID;
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String playerName) {
        this.ownerName = playerName;
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);

        if (graveId != null) {
            tag.putUUID("grave_id", graveId);
        }
        if (ownerId != null) {
            tag.putUUID("owner_id", ownerId);
        }
        if (ownerName != null) {
            tag.putString("owner_name", ownerName);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.hasUUID("grave_id")) {
            graveId = tag.getUUID("grave_id");
        }
        if (tag.hasUUID("owner_id")) {
            ownerId = tag.getUUID("owner_id");
        }
        if (tag.contains("owner_name")) {
            ownerName = tag.getString("owner_name");
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GraveStoneBlockEntity be) {

    }
}
