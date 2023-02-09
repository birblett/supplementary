package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.lib.components.*;
import com.birblett.lib.helper.RenderHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.birblett.Supplementary.MODID;

public class SupplementaryComponents implements EntityComponentInitializer {

    public static final ComponentKey<BaseComponent> BURST_FIRE_TIMER =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "burst_fire_timer"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> GRAPPLING =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "grappling"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> GRAPPLING_TRACKING_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "grappling_tracking_component"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> IGNORES_IFRAMES =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "ignores_iframes"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> LIGHTNING_BOLT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "lightning_bolt"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> MARKED_LEVEL =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> MARKED_TRACKED_ENTITY =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked_tracked_entity"), BaseComponent.class);

    public static final List<ComponentKey<BaseComponent>> ENTITY_TICKING_COMPONENTS = List.of(
            BURST_FIRE_TIMER
    );
    public static final List<ComponentKey<BaseComponent>> PLAYER_TICKING_COMPONENTS = List.of(
            GRAPPLING_TRACKING_COMPONENT
    );
    public static final List<ComponentKey<BaseComponent>> PROJECTILE_COMPONENTS = List.of(
            IGNORES_IFRAMES,
            LIGHTNING_BOLT,
            MARKED_LEVEL,
            GRAPPLING
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PersistentProjectileEntity.class, BURST_FIRE_TIMER, e -> new TimedComponent("burst_fire_timer") {
            @Override
            public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {
                if (this.getValue() == 0) {
                    this.itemStack = stack;
                    this.hand = hand;
                    this.storedProjectile = savedProjectile;
                    this.setValue(8);
                }
            }

            @Override
            public void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {
                projectileEntity.setDamage(projectileEntity.getDamage() - 0.3);
                SupplementaryComponents.IGNORES_IFRAMES.get(projectileEntity).setValue(1);
            }

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
                    this.hand = null;
                }
            }
        }); // projectile component of burst fire
        registry.registerFor(LivingEntity.class, BURST_FIRE_TIMER, e -> new TimedComponent("burst_fire_timer") {
            @Override
            public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {
                if (this.getValue() == 0) {
                    this.itemStack = stack;
                    this.hand = hand;
                    this.storedProjectile = savedProjectile;
                    this.setValue(8);
                }
            }

            @Override
            public void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {
                projectileEntity.setDamage(projectileEntity.getDamage() - 0.3);
                SupplementaryComponents.IGNORES_IFRAMES.get(projectileEntity).setValue(1);
                Supplementary.LOGGER.info("{} {}", IGNORES_IFRAMES.getId(), IGNORES_IFRAMES.get(projectileEntity).getValue());
            }

            @Override
            public void onTick(LivingEntity livingEntity) {
                if (this.hand != null && livingEntity.getStackInHand(this.hand) == this.itemStack && this.getValue() > 0) {
                    this.setValue(this.getValue() - 1);
                    if (this.getValue() % 4 == 0) {
                        float pitch = CrossbowItem.getSoundPitch(true, livingEntity.getRandom());
                        CrossbowItem.shoot(livingEntity.getWorld(), livingEntity, hand, this.itemStack, this.storedProjectile,
                                pitch, true,
                                CrossbowItem.getSpeed(this.storedProjectile), livingEntity.isSneaking() ? 3.0F : 6.0F,
                                0.0F);
                    }
                }
                else if (this.getValue() > 0) {
                    this.setValue(0);
                    this.hand = null;
                }
            }
        }); // shooter component of burst fire
        registry.registerFor(PersistentProjectileEntity.class, GRAPPLING, e -> new SyncedEnchantmentComponent("grappling") {
            @Override
            public void inBlockTick(PersistentProjectileEntity persistentProjectileEntity, int level) {
                if (!persistentProjectileEntity.world.isClient()) {
                    Entity owner = persistentProjectileEntity.getOwner();
                    if (owner instanceof LivingEntity livingEntity && owner.isAlive() && owner.getWorld() == persistentProjectileEntity.getWorld() &&
                            persistentProjectileEntity.getOwner().getPos().subtract(persistentProjectileEntity.getPos()).lengthSquared() < 2500 &&
                            EnchantmentHelper.getLevel(SupplementaryEnchantments.GRAPPLING, livingEntity.getMainHandStack()) > 0 &&
                            GRAPPLING_TRACKING_COMPONENT.get(owner).getEntity() == persistentProjectileEntity) {
                        double pullSpeed = owner.isTouchingWater() ? 0.1 : 0.4;
                        owner.setVelocity(persistentProjectileEntity.getPos().subtract(owner.getPos()).normalize().multiply(pullSpeed).add(owner.getVelocity()));
                        owner.velocityModified = true;
                        if (persistentProjectileEntity.getOwner().getPos().subtract(persistentProjectileEntity.getPos()).lengthSquared() < 2) {
                            this.setValue(0);
                        }
                    } else {
                        this.setValue(0);
                        GRAPPLING.sync(persistentProjectileEntity);
                    }
                }
            }

            @Override
            public void onProjectileFire(LivingEntity user, PersistentProjectileEntity persistentProjectileEntity, int level) {
                if (!persistentProjectileEntity.world.isClient()) {
                    SupplementaryComponents.GRAPPLING.get(persistentProjectileEntity).setValue(1);
                    GRAPPLING_TRACKING_COMPONENT.get(user).setEntity(persistentProjectileEntity);
                    GRAPPLING.sync(persistentProjectileEntity);
                }
            }

            @Override
            public Vec3d onTravel(PersistentProjectileEntity persistentProjectileEntity, int level, Vec3d velocity) {
                if (!persistentProjectileEntity.world.isClient()) {
                    persistentProjectileEntity.ignoreCameraFrustum = true;
                    GRAPPLING.sync(persistentProjectileEntity);
                }
                return velocity;
            }

            @Override
            public void onProjectileRender(PersistentProjectileEntity persistentProjectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level) {
                Entity owner = persistentProjectileEntity.getOwner();
                if (owner instanceof LivingEntity livingEntity && owner.isAlive() && owner.getWorld() == persistentProjectileEntity.getWorld() &&
                        persistentProjectileEntity.getOwner().getPos().subtract(persistentProjectileEntity.getPos()).lengthSquared() < 2500 &&
                        EnchantmentHelper.getLevel(SupplementaryEnchantments.GRAPPLING, livingEntity.getMainHandStack()) > 0) {
                    RenderHelper.ropeRender(persistentProjectileEntity, tickDelta, matrixStack, vertexConsumerProvider, owner);
                } else {
                    this.setValue(0);
                    GRAPPLING.sync(persistentProjectileEntity);
                }
            }
        });
        registry.registerFor(LivingEntity.class, GRAPPLING_TRACKING_COMPONENT, e -> new TrackingComponent() {
            @Override
            public void onHandSwingEvent(LivingEntity entity, Hand hand) {
                if (!entity.getWorld().isClient()) {
                    this.setEntity(null);
                }
            }

            @Override
            public void onUse(LivingEntity entity, Hand hand) {
                if (!entity.getWorld().isClient()) {
                    this.setEntity(null);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, IGNORES_IFRAMES, e -> new EnchantmentComponent("ignores_iframes") {
            @Override
            public void preEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                if (target instanceof LivingEntity livingEntity) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 1;
                }
                else if (target instanceof EnderDragonPart dragonPart) {
                    dragonPart.owner.hurtTime = 0;
                    dragonPart.timeUntilRegen = 1;
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new EnchantmentComponent("lightning_bolt") {
            @Override
            public void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                if (target instanceof LivingEntity livingEntity && target.getWorld().isSkyVisible(target.getBlockPos())) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 1;
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                    lightning.setPosition(target.getPos());
                    target.getWorld().spawnEntity(lightning);
                }
            }

            @Override
            public void onBlockHit(BlockHitResult blockHitResult, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                if (persistentProjectileEntity.getWorld().isSkyVisible(persistentProjectileEntity.getBlockPos())) {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, persistentProjectileEntity.getWorld());
                    lightning.setPosition(persistentProjectileEntity.getPos());
                    persistentProjectileEntity.getWorld().spawnEntity(lightning);
                    this.setValue(0);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, MARKED_LEVEL, e -> new EnchantmentComponent("marked") {
            @Override
            public void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {
                // 10 ticks of tracking per level
                this.setValue(level * 10);
                Entity target = SupplementaryComponents.MARKED_TRACKED_ENTITY.get(user).getEntity();
                if (target instanceof LivingEntity || target instanceof EnderDragonPart) {
                    SupplementaryComponents.MARKED_TRACKED_ENTITY.get(projectileEntity).setEntity(target);
                }
            }

            @Override
            public Vec3d onTravel(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {
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
                        // calculate angle between arrow and tracked mob, adjust course up to pi/8 radians per tick
                        double angle = Math.asin(Math.sqrt(normVelocity.crossProduct(projectileToTarget).lengthSquared() /
                                        (normVelocity.lengthSquared() * projectileToTarget.lengthSquared())));
                        angle = Math.min(angle, Math.PI / 8);
                        // return an adjusted vector
                        this.decrement();
                        return velocity.multiply(Math.cos(angle)).add(normal.multiply(Math.sin(angle))).normalize()
                                .multiply(velocity.length());
                    }
                }
                return velocity;
            }

            @Override
            public void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {
                if (persistentProjectileEntity.getOwner() instanceof LivingEntity owner && (target instanceof LivingEntity || target instanceof EnderDragonPart)) {
                    MARKED_TRACKED_ENTITY.get(owner).setEntity(target);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, MARKED_TRACKED_ENTITY, e -> new TrackingComponent());
        registry.registerFor(LivingEntity.class, MARKED_TRACKED_ENTITY, e -> new TrackingComponent());
    }
}
