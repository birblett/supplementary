package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public interface IntComponent extends Component {

    int getValue();
    void setValue(int level);
    void preEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile);
    void onTick(LivingEntity entity);
    Vec3d onProjectileTick(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity);
    void increment();
}

