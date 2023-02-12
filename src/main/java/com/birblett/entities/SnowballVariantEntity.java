package com.birblett.entities;

import com.birblett.items.SnowballVariantItem;
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
    /*
    Base class for all snowball variant entities.

    Methods
        onEntityHit(EntityHitResult) - executes onEntityHitEvent provided by the stored SnowballVariantItem
        onBlockHit(BlockHitResult) - executes onBlockHitEvent provided by the stored SnowballVariantItem
        onCollision(HitResult) - simplified version of SnowballEntity onCollision logic

    Inherited/overridden methods
        getDefaultItem() - returns Items.SNOWBALL; serves no practical function
        shouldRender(double) - returns whether in distance to be rendered; usually obsolete due to lower tracking range
     */

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

    protected void onEntityHit(EntityHitResult entityHitResult) {
        /*
        calls SnowVariantItem$onEntityHitEvent if stored item is of matching type
         */
        super.onEntityHit(entityHitResult);
        if (this.getItem().getItem() instanceof SnowballVariantItem item) {
            item.onEntityHitEvent(entityHitResult.getEntity(), this);
        }
    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        /*
        calls SnowVariantItem$onBlockHitEvent if stored item is of matching type
         */
        super.onBlockHit(blockHitResult);
        if (this.getItem().getItem() instanceof SnowballVariantItem item) {
            item.onBlockHitEvent(blockHitResult, this);
        }
    }

    protected void onCollision(HitResult hitResult) {
        /*
        discard this entity post-collision unless removeAfterCollision is set to false in the parent item
         */
        super.onCollision(hitResult);
        if (!this.world.isClient && this.getItem().getItem() instanceof SnowballVariantItem item && item.removeAfterCollision()) {
            this.world.sendEntityStatus(this, (byte)3);
            this.kill();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < 96*96;
    }
}
