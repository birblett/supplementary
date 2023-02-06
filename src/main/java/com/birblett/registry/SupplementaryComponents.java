package com.birblett.registry;

import com.birblett.lib.components.*;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;
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

    public static final ComponentKey<IntComponent> BURST_FIRE_TIMER =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "burst_fire_timer"), IntComponent.class);
    public static final ComponentKey<IntComponent> LIGHTNING_BOLT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "lightning_bolt"), IntComponent.class);
    public static final ComponentKey<IntComponent> MARKED_LEVEL =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"), IntComponent.class);
    public static final ComponentKey<EntityComponent> MARKED_TRACKED_ENTITY =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked_tracked_entity"), EntityComponent.class);

    public static final List<ComponentKey<IntComponent>> ENTITY_TICKING_COMPONENTS = List.of(
            BURST_FIRE_TIMER
    );
    public static final List<ComponentKey<IntComponent>> PROJECTILE_COMPONENTS = List.of(
            LIGHTNING_BOLT,
            MARKED_LEVEL
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, BURST_FIRE_TIMER, e -> new TimedComponent("burst_fire_timer") {
            @Override
            public void onTick(LivingEntity livingEntity) {
                if (this.hand != null && livingEntity.getStackInHand(this.hand) == this.itemStack && this.getValue() > 0) {
                    this.setValue(this.getValue() - 1);
                    if (this.getValue() % 4 == 0) {
                        float pitch = CrossbowItem.getSoundPitch(true, livingEntity.getRandom());
                        CrossbowItem.shoot(livingEntity.getWorld(), livingEntity, hand, this.itemStack, this.storedProjectile,
                                pitch, livingEntity instanceof PlayerEntity player && player.isCreative(),
                                CrossbowItem.getSpeed(this.storedProjectile), 1.0F, 0.0F);
                    }
                }
                else if (this.getValue() > 0) {
                    this.setValue(0);
                }
            }

        });
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new EnchantmentComponent("lightning_bolt") {
            @Override
            public void onEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                // summon lightning only if skylight visible
                if (target instanceof LivingEntity livingEntity && target.getWorld().isSkyVisible(target.getBlockPos())) {
                    // resets iframes so lightning actually damages target entities
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 1;
                    // summon lightning at target pos
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                    lightning.setCosmetic(true);
                    lightning.setPosition(target.getPos());
                    target.getWorld().spawnEntity(lightning);
                }
            }
        });
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
                        double angle = Math.asin(Math.sqrt(normVelocity.crossProduct(projectileToTarget).lengthSquared() /
                                        (normVelocity.lengthSquared() * projectileToTarget.lengthSquared())));
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
