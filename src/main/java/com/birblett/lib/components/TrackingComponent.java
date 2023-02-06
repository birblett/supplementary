package com.birblett.lib.components;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class TrackingComponent implements EntityComponent {

    private Entity trackedEntity = null;

    @Override
    public Entity getEntity() {
        return trackedEntity;
    }

    @Override
    public void setEntity(Entity entity) {
        this.trackedEntity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
}
