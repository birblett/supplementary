package com.birblett.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class BoomerangEntity extends ProjectileEntity {

    private static final TrackedData<Integer> PIERCING = DataTracker.registerData(BoomerangEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public BoomerangEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(PIERCING, 0);
    }
}
