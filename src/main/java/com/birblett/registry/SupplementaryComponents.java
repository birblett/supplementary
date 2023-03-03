package com.birblett.registry;

import com.birblett.lib.components.*;
import com.birblett.lib.components.SimpleEntityComponent;
import com.birblett.lib.helper.EntityHelper;
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
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.birblett.Supplementary.MODID;

public class SupplementaryComponents implements EntityComponentInitializer {

    public static final ComponentKey<BaseComponent> ASSAULT_DASH =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "assault_dash"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> BURST_FIRE =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "burst_fire"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> GRAPPLING =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "grappling"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> IGNORES_IFRAMES =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "ignores_iframes"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> LIGHTNING_BOLT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "lightning_bolt"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> MARKED =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"), BaseComponent.class);
    public static final ComponentKey<BaseComponent> OVERSIZED =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "oversized"), BaseComponent.class);
    @SuppressWarnings("rawtypes")
    public static final ComponentKey<SimpleComponent> SNOWBALL_TYPE =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "snowball_type"), SimpleComponent.class);

    public static final List<ComponentKey<BaseComponent>> ENTITY_TICKING_COMPONENTS = List.of(
            ASSAULT_DASH,
            BURST_FIRE
    );
    public static final List<ComponentKey<BaseComponent>> PROJECTILE_COMPONENTS = List.of(
            IGNORES_IFRAMES,
            LIGHTNING_BOLT,
            MARKED,
            OVERSIZED,
            GRAPPLING
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ASSAULT_DASH, e -> new EnchantmentComponent("assault_dash") {
            private Vec3d initialVelocity;

            @Override
            public void setValue(int level, Entity user) {
                this.setValue(10);
                initialVelocity = user.getRotationVecClient().multiply(1, 0, 1).normalize().multiply(0.6 + level * 0.3);
            }

            @Override
            public Vec3d onEntityTravel(Entity entity, int level, Vec3d velocity) {
                if (this.getValue() > 0 && entity instanceof PlayerEntity user) {
                    Vec3d userVelocity = initialVelocity;
                    Item item = user.getActiveItem().getItem();
                    if (item.getMaxUseTime(user.getActiveItem()) - user.getItemUseTimeLeft() >= 0 && user.isUsingItem() &&
                            item.getUseAction(user.getActiveItem()) == UseAction.BLOCK) {
                        List<EntityHitResult> entityHitResults = EntityHelper.getEntityCollisions(user.world, user, user.getPos().subtract(this.initialVelocity),
                                user.getPos().add(this.initialVelocity), user.getBoundingBox().stretch(this.initialVelocity).expand(1.0),
                                e -> true, 0.5f);
                        for (EntityHitResult entityHitResult : entityHitResults) {
                            Entity target = entityHitResult.getEntity();
                            if (target.damage(SupplementaryEnchantments.shieldBash(user), (float) this.initialVelocity.length() * 2)) {
                                target.setVelocity(target.getVelocity().add(this.initialVelocity.multiply(1.2)).add(0, 0.2, 0));
                                if (target instanceof PlayerEntity) {
                                    target.velocityModified = true;
                                }
                                if (user instanceof ServerPlayerEntity) {
                                    if (!user.getAbilities().creativeMode) {
                                        user.getActiveItem().damage(1, user.getRandom(), (ServerPlayerEntity) user);
                                    }
                                    user.world.playSoundFromEntity(null, target, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS,
                                            1.0f, user.world.random.nextFloat() * 0.4f);
                                }
                            }
                        }
                        if (this.getValue() < 8) {
                            this.initialVelocity = this.initialVelocity.multiply(0.75);
                        }
                        this.decrement();
                    }
                    else {
                        this.setValue(0);
                        userVelocity = userVelocity.multiply(0.3);
                    }
                    velocity = userVelocity;
                }
                return velocity;
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, BURST_FIRE, e -> new EnchantmentComponent("burst_fire") {

            private ItemStack itemStack = ItemStack.EMPTY;
            private Hand hand = null;
            private ItemStack storedProjectile = ItemStack.EMPTY;

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
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() - 0.3);
                    SupplementaryComponents.IGNORES_IFRAMES.get(projectileEntity).setValue(1);
                    persistentProjectileEntity.setVelocity(persistentProjectileEntity.getVelocity().multiply(0.7));
                    if (user instanceof PlayerEntity player) {
                        player.getItemCooldownManager().set(user.getActiveItem().getItem(), 30);
                    }
                }
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
        });
        registry.registerFor(LivingEntity.class, BURST_FIRE, e -> new EnchantmentComponent("burst_fire") {

            private ItemStack itemStack = ItemStack.EMPTY;
            private Hand hand = null;
            private ItemStack storedProjectile = ItemStack.EMPTY;

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
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() - 0.3);
                    SupplementaryComponents.IGNORES_IFRAMES.get(projectileEntity).setValue(1);
                }
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
            public void inBlockTick(ProjectileEntity projectileEntity, int level) {
                if (!projectileEntity.world.isClient()) {
                    Entity owner = projectileEntity.getOwner();
                    if (owner instanceof LivingEntity livingEntity && owner.isAlive() && owner.getWorld() == projectileEntity.getWorld() &&
                            projectileEntity.getOwner().getPos().subtract(projectileEntity.getPos()).lengthSquared() < 2500 &&
                            EnchantmentHelper.getLevel(SupplementaryEnchantments.GRAPPLING, livingEntity.getMainHandStack()) > 0 &&
                            GRAPPLING.get(owner).getEntity() == projectileEntity) {
                        double pullSpeed = owner.isTouchingWater() ? 0.1 : 0.4;
                        owner.setVelocity(projectileEntity.getPos().subtract(owner.getPos()).normalize().multiply(pullSpeed).add(owner.getVelocity()));
                        owner.velocityModified = true;
                        if (projectileEntity.getOwner().getPos().subtract(projectileEntity.getPos()).lengthSquared() < 2) {
                            this.setValue(0);
                            GRAPPLING.sync(projectileEntity);
                        }
                    } else {
                        this.setValue(0);
                        GRAPPLING.sync(projectileEntity);
                    }
                }
            }

            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity persistentProjectileEntity, int level) {
                if (!persistentProjectileEntity.world.isClient()) {
                    Entity lastProjectile;
                    if ((lastProjectile = GRAPPLING.get(user).getEntity()) != null && lastProjectile instanceof PersistentProjectileEntity) {
                        GRAPPLING.get(lastProjectile).setValue(0);
                        GRAPPLING.sync(lastProjectile);
                    }
                    SupplementaryComponents.GRAPPLING.get(persistentProjectileEntity).setValue(1);
                    GRAPPLING.get(user).setEntity(persistentProjectileEntity);
                    GRAPPLING.sync(persistentProjectileEntity);
                }
            }

            @Override
            public Vec3d onEntityTravel(Entity persistentProjectileEntity, int level, Vec3d velocity) {
                if (!persistentProjectileEntity.world.isClient()) {
                    GRAPPLING.sync(persistentProjectileEntity);
                }
                persistentProjectileEntity.ignoreCameraFrustum = true;
                return velocity;
            }

            @Override
            public void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level) {
                Entity owner = projectileEntity.getOwner();
                if (owner instanceof LivingEntity livingEntity && owner.isAlive() && owner.getWorld() == projectileEntity.getWorld() &&
                        projectileEntity.getOwner().getPos().subtract(projectileEntity.getPos()).lengthSquared() < 2500 &&
                        EnchantmentHelper.getLevel(SupplementaryEnchantments.GRAPPLING, livingEntity.getMainHandStack()) > 0 &&
                        (livingEntity.getMainHandStack().getItem() instanceof CrossbowItem || livingEntity.getMainHandStack().getItem()
                        instanceof BowItem)) {
                    RenderHelper.ropeRender(projectileEntity, tickDelta, matrixStack, vertexConsumerProvider, owner);
                } else {
                    this.setValue(0);
                    GRAPPLING.sync(projectileEntity);
                }
            }
        });
        registry.registerFor(FishingBobberEntity.class, GRAPPLING, e -> new EnchantmentComponent("grappling") {
            @Override
            public void inBlockTick(ProjectileEntity projectileEntity, int level) {
                this.setValue(2);
                projectileEntity.setVelocity(new Vec3d(0.0, 0.03, 0.0));
                projectileEntity.setYaw(0);
                projectileEntity.setPitch(0);
                projectileEntity.velocityModified = true;
            }

            @Override
            public void onTick(LivingEntity livingEntity) {
                this.setValue(1);
            }

            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
                this.setValue(1);
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                if ((this.getValue() == 2 || target != null) && projectileEntity.getOwner() instanceof PlayerEntity playerEntity &&
                        !playerEntity.getWorld().isClient()){
                    double touchingWaterPullSpeed = playerEntity.isTouchingWater() ? 0.4 : 1.2;
                    Vec3d pullStrength = projectileEntity.getPos().subtract(playerEntity.getPos()).normalize().multiply(touchingWaterPullSpeed);
                    playerEntity.setVelocity(playerEntity.getVelocity().add(pullStrength));
                    playerEntity.velocityModified = true;
                }
                return target != null;
            }
        });
        registry.registerFor(LivingEntity.class, GRAPPLING, e -> new EnchantmentComponent() {
            private Entity trackedEntity;

            @Override
            public Entity getEntity() {
                return trackedEntity;
            }

            @Override
            public void setEntity(Entity entity) {
                trackedEntity = entity;
            }

            @Override
            public void onHandSwingEvent(LivingEntity entity, Hand hand) {
                if (!entity.getWorld().isClient() && this.getEntity() instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    GRAPPLING.get(persistentProjectileEntity).setValue(0);
                    GRAPPLING.sync(persistentProjectileEntity);
                    this.setEntity(null);
                }
            }

            @Override
            public void onUse(LivingEntity entity, Hand hand) {
                if (!entity.getWorld().isClient() && this.getEntity() instanceof PersistentProjectileEntity) {
                    this.setEntity(null);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new EnchantmentComponent("lightning_bolt") {
            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                if (target instanceof LivingEntity livingEntity && target.getWorld().isSkyVisible(target.getBlockPos())) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 1;
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                    lightning.setPosition(target.getPos());
                    target.getWorld().spawnEntity(lightning);
                }
                return false;
            }

            @Override
            public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity projectileEntity, int lvl) {
                if (projectileEntity.getWorld().isSkyVisible(projectileEntity.getBlockPos())) {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, projectileEntity.getWorld());
                    lightning.setPosition(projectileEntity.getPos());
                    projectileEntity.getWorld().spawnEntity(lightning);
                    this.setValue(0);
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, MARKED, e -> new EnchantmentComponent("marked") {
            private Entity trackedEntity;

            @Override
            public Entity getEntity() {
                return trackedEntity;
            }

            @Override
            public void setEntity(Entity entity) {
                trackedEntity = entity;
            }

            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
                // 10 ticks of tracking per level
                this.setValue(level * 10);
                Entity target = MARKED.get(user).getEntity();
                if (target instanceof LivingEntity || target instanceof EnderDragonPart) {
                    this.setEntity(target);
                }
            }

            @Override
            public Vec3d onEntityTravel(Entity entity, int level, Vec3d velocity) {
                if (entity instanceof PersistentProjectileEntity projectile &&
                        projectile.getOwner() instanceof LivingEntity owner && projectile.isCritical()) {
                    Entity target = MARKED.get(owner).getEntity();
                    if (target != null && target.isAlive() && target.getWorld() == entity.getWorld()) {
                        Vec3d projectilePos = entity.getPos();
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
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                if (projectileEntity.getOwner() instanceof LivingEntity owner && (target instanceof LivingEntity || target instanceof EnderDragonPart)) {
                    MARKED.get(owner).setEntity(target);
                }
                return false;
            }
        });
        registry.registerFor(LivingEntity.class, MARKED, e -> new EnchantmentComponent() {
            private Entity trackedEntity;

            @Override
            public Entity getEntity() {
                return trackedEntity;
            }

            @Override
            public void setEntity(Entity entity) {
                trackedEntity = entity;
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, OVERSIZED, e -> new SyncedEnchantmentComponent("oversized") {
            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    this.setValue(level);
                    projectileEntity.setVelocity(projectileEntity.getVelocity().multiply(Math.pow(1.3, level)));
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + 0.5 * level);
                    OVERSIZED.sync(projectileEntity);
                }
            }

            @Override
            public void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level) {
                float scale = 1.3f + level * 0.2f;
                matrixStack.scale(scale, scale, scale);
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                if (target instanceof PlayerEntity player && player.isBlocking()) {
                    ((LivingEntity) player).damageShield(2.0f);
                    player.disableShield(false);
                }
                return false;
            }
        });

        registry.registerFor(PersistentProjectileEntity.class, IGNORES_IFRAMES, e -> new EnchantmentComponent("ignores_iframes") {
            @Override
            public void preEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
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
        registry.registerFor(SnowGolemEntity.class, SNOWBALL_TYPE, e -> new SimpleEntityComponent<Integer>("snowball_type"));
    }
}
