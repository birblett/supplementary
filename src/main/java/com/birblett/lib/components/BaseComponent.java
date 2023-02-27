package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public interface BaseComponent extends Component {

    int getValue();
    void setValue(int level);
    void decrement();
    void increment();
    Entity getEntity();
    void setEntity(Entity entity);
    void inBlockTick(ProjectileEntity projectileEntity, int lvl);
    void preEntityHit(Entity target, ProjectileEntity persistentProjectileEntity, int lvl);
    boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl);
    void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity persistentProjectileEntity, int lvl);
    void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile);
    void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level);
    void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level);
    void onHandSwingEvent(LivingEntity entity, Hand hand);
    void onUse(LivingEntity entity, Hand hand);
    void onTick(LivingEntity entity);
    Vec3d onProjectileTravel(ProjectileEntity projectileEntity, int level, Vec3d velocity);
}

