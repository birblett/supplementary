package com.birblett.entities;

import com.birblett.items.AbstractSnowballVariantItem;
import com.birblett.registry.SupplementaryEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SnowballVariantEntity extends ThrownItemEntity {

    private int shouldRenderDistance = 96*96;

    public interface SnowballVariantHitEvent {
        void execute(LivingEntity target, SnowballVariantEntity snowballVariantEntity);
    }

    public SnowballVariantEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowballVariantEntity(World world, Vec3d pos) {
        super(SupplementaryEntities.SNOWBALL_VARIANT, world);
        this.setPosition(pos);
    }

    public SnowballVariantEntity(World world, LivingEntity user) {
        super(SupplementaryEntities.SNOWBALL_VARIANT, user, world);
        this.setPosition(user.getEyePos());
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.getItem().getItem() instanceof AbstractSnowballVariantItem item) {
            item.onEntityHitEvent(entityHitResult.getEntity(), this);
        }
    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.getItem().getItem() instanceof AbstractSnowballVariantItem item) {
            item.onBlockHitEvent(blockHitResult, this);
        }
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient && this.getItem().getItem() instanceof AbstractSnowballVariantItem item && !item.persistsAfterCollision()) {
            this.world.sendEntityStatus(this, (byte)3);
            this.kill();
        }
    }

    public void setShouldRenderDistance(int distance) {
        this.shouldRenderDistance = distance;
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < this.shouldRenderDistance;
    }
}
