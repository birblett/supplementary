package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public interface BaseComponent extends Component {

    int getValue();
    void setValue(int level);
    Entity getEntity();
    void setEntity(Entity entity);
    void inBlockTick(PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void preEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void onBlockHit(BlockHitResult blockHitResult, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile);
    void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level);
    void onProjectileRender(PersistentProjectileEntity persistentProjectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level);
    void onHandSwingEvent(LivingEntity entity, Hand hand);
    void onUse(LivingEntity entity, Hand hand);
    void onTick(LivingEntity entity);
    Vec3d onTravel(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity);
    void decrement();
    void increment();
}

