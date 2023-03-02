package com.birblett.lib.components;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class EntityTrackingComponent implements BaseComponent {

    private Entity trackedEntity;

    @Override
    public Entity getEntity() {
        return trackedEntity;
    }

    @Override
    public void setEntity(Entity entity) {
        trackedEntity = entity;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {}

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {}
}
