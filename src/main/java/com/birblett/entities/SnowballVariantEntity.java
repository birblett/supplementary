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
import net.minecraft.world.World;

public class SnowballVariantEntity extends ThrownItemEntity {

    public SnowballVariantEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowballVariantEntity(World world, double x, double y, double z) {
        super(SupplementaryEntities.SNOWBALL_VARIANT, world);
        this.setPosition(x, y, z);
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

    @Override
    public boolean shouldRender(double distance) {
        return distance < 96*96;
    }
}
