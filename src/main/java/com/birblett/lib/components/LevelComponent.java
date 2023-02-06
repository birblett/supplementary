package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;

public interface LevelComponent extends Component {

    int getValue();
    void setValue(int level);
    void onEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl);
    Vec3d onProjectileTick(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity);
    void increment();
}

