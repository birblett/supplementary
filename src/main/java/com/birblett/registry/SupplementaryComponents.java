package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.lib.components.EntityComponent;
import com.birblett.lib.components.LevelComponent;
import com.birblett.lib.components.EnchantmentComponent;
import com.birblett.lib.components.TrackingComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.birblett.Supplementary.MODID;

public class SupplementaryComponents implements EntityComponentInitializer {

    public enum ComponentType {
        NONE,
        ARROW,
        ENTITY,
        BLOCK_ENTITY
    }

    public static final ComponentKey<LevelComponent> LIGHTNING_BOLT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "lightning_bolt"), LevelComponent.class);
    public static final ComponentKey<LevelComponent> MARKED_LEVEL =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"), LevelComponent.class);
    public static final ComponentKey<EntityComponent> MARKED_TRACKED_ENTITY =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked_tracked_entity"), EntityComponent.class);

    public static final List<ComponentKey<LevelComponent>> PROJECTILE_COMPONENTS = List.of(LIGHTNING_BOLT, MARKED_LEVEL);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new EnchantmentComponent("lightning_bolt"));
        registry.registerFor(PersistentProjectileEntity.class, MARKED_LEVEL, e -> new EnchantmentComponent("marked") {
            @Override
            public Vec3d onProjectileTick(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {
                if (persistentProjectile.getOwner() instanceof LivingEntity owner && persistentProjectile.isCritical()) {
                    Entity target = MARKED_TRACKED_ENTITY.get(owner).getEntity();
                    if (target != null && target.isAlive() && target.getWorld() == persistentProjectile.getWorld()) {
                        Vec3d projectilePos = persistentProjectile.getPos();
                        Vec3d targetPos = target.getEyePos();
                        Vec3d projectileToTarget = new Vec3d(targetPos.x - projectilePos.x, targetPos.y -
                                projectilePos.y, targetPos.z - projectilePos.z);
                        Vec3d normVelocity = velocity.normalize();
                        // calculation of the normal between projectile velocity and vector to target position
                        Vec3d normal = normVelocity.crossProduct(projectileToTarget).crossProduct(normVelocity).normalize();
                        // calculate angle between arrow and tracked mob, adjust up to pi/18 * ench lvl radians per tick
                        double angle = Math.asin((normVelocity.crossProduct(projectileToTarget).length() /
                                        (normVelocity.length() * projectileToTarget.length())));
                        angle = Math.min(angle, Math.PI / 18 * level);
                        // return an adjusted vector
                        return velocity.multiply(Math.cos(angle)).add(normal.multiply(Math.sin(angle))).normalize()
                                .multiply(velocity.length());
                    }
                }
                return velocity;
            }

            @Override
            public void onEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                if (persistentProjectileEntity.getOwner() instanceof LivingEntity owner && (target instanceof LivingEntity || target instanceof EnderDragonPart)) {
                    MARKED_TRACKED_ENTITY.get(owner).setEntity(target);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, MARKED_TRACKED_ENTITY, e -> new TrackingComponent());
        registry.registerFor(LivingEntity.class, MARKED_TRACKED_ENTITY, e -> new TrackingComponent());
    }
}
