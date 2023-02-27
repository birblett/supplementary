package com.birblett.lib.components;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public class TrackingComponent implements BaseComponent {

    private Entity trackedEntity;

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int level) {}

    @Override
    public Entity getEntity() {
        return trackedEntity;
    }

    @Override
    public void setEntity(Entity entity) {
        trackedEntity = entity;
    }

    @Override
    public void inBlockTick(ProjectileEntity projectileEntity, int lvl) {}

    @Override
    public void preEntityHit(Entity target, ProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
        return false;
    }

    @Override
    public void onHandSwingEvent(LivingEntity entity, Hand hand) {}

    @Override
    public void onUse(LivingEntity entity, Hand hand) {}

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    @Override
    public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {}

    @Override
    public void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @Override
    public Vec3d onProjectileTravel(ProjectileEntity projectileEntity, int level, Vec3d velocity) {
        return velocity;
    }

    @Override
    public void decrement() {}

    @Override
    public void increment() {}

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
}
