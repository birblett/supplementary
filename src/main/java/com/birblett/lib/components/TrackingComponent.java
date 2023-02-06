package com.birblett.lib.components;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class TrackingComponent implements EntityComponent {

    private LivingEntity trackedEntity = null;

    @Override
    public LivingEntity getEntity() {
        return trackedEntity;
    }

    @Override
    public void setEntity(LivingEntity livingEntity) {
        this.trackedEntity = livingEntity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
}
