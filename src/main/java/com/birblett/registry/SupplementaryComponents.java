package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.lib.components.*;
import com.birblett.lib.helper.EnchantHelper;
import com.birblett.lib.helper.EntityHelper;
import com.birblett.lib.helper.RenderHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.birblett.Supplementary.MODID;

/**
 * Components used to track data/events on entities, primarily for enchantments.
 */
public class SupplementaryComponents implements EntityComponentInitializer {

    /**
     * Adaptability enchantment's half-health buff effect
     */
    public static final ComponentKey<BaseComponent> ADAPTABILITY = ComponentRegistry.getOrCreate(new Identifier(MODID, "adaptability"),
            BaseComponent.class);
    /**
     * Assault Dash enchantment's dashing and damaging.
     */
    public static final ComponentKey<BaseComponent> ASSAULT_DASH = ComponentRegistry.getOrCreate(new Identifier(MODID, "assault_dash"),
            BaseComponent.class);
    /**
     * Track Blighted curse status for players.
     */
    @SuppressWarnings("rawtypes")
    public static final ComponentKey<SimpleComponent> BLIGHTED = ComponentRegistry.getOrCreate(new Identifier(MODID, "blighted"),
            SimpleComponent.class);
    /**
     * Burst Fire timing and firing after initial use.
     */
    public static final ComponentKey<BaseComponent> BURST_FIRE = ComponentRegistry.getOrCreate(new Identifier(MODID, "burst_fire"),
            BaseComponent.class);
    /**
     * Handles Enhanced functionality for PersistentProjectileEntities.
     */
    public static final ComponentKey<BaseComponent> ENHANCED = ComponentRegistry.getOrCreate(new Identifier(MODID, "enhanced"),
            BaseComponent.class);
    /**
     * Handles Grappling functionality; individual functionalities registered for PersistentProjectileEntity,
     * FishingBobberEntity, and LivingEntity.
     */
    public static final ComponentKey<BaseComponent> GRAPPLING = ComponentRegistry.getOrCreate(new Identifier(MODID, "grappling"),
            BaseComponent.class);
    /**
     * Tracks whether a phantom was spawned by a player wearing Haunted gear.
     */
    public static final ComponentKey<BaseComponent> HAUNTED = ComponentRegistry.getOrCreate(new Identifier(MODID, "haunted"),
            BaseComponent.class);
    /**
     * Handles Hitscan functionality.
     */
    public static final ComponentKey<BaseComponent> HITSCAN = ComponentRegistry.getOrCreate(new Identifier(MODID, "hitscan"),
            BaseComponent.class);
    /**
     * Set value of this component to 1 on arrows to set invincibility frame ignoring property.
     */
    public static final ComponentKey<BaseComponent> IGNORES_IFRAMES = ComponentRegistry.getOrCreate(new Identifier(MODID,
            "ignores_iframes"), BaseComponent.class);
    /**
     * Handles Lightning Bolt projectile functionality.
     */
    public static final ComponentKey<BaseComponent> LIGHTNING_BOLT = ComponentRegistry.getOrCreate(new Identifier(MODID,
            "lightning_bolt"), BaseComponent.class);
    /**
     * Handles entity tracking and homing functionality for Marked.
     */
    public static final ComponentKey<BaseComponent> MARKED = ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"),
            BaseComponent.class);
    /**
     * Handles random fluctuation of Moody.
     */
    public static final ComponentKey<BaseComponent> MOODY = ComponentRegistry.getOrCreate(new Identifier(MODID, "moody"),
            BaseComponent.class);
    /**
     * Oversized rendering and damage/velocity increase.
     */
    public static final ComponentKey<BaseComponent> OVERSIZED = ComponentRegistry.getOrCreate(new Identifier(MODID, "oversized"),
            BaseComponent.class);
    /**
     * Allows snowball type of a snow golem to be tracked; logic for actually firing the snowball is handled in
     * {@link com.birblett.mixin.snowball_variants.SnowballVariantsGolemMixin}.
     */
    @SuppressWarnings("rawtypes")
    public static final ComponentKey<SimpleComponent> SNOWBALL_TYPE = ComponentRegistry.getOrCreate(new Identifier(MODID,
            "snowball_type"), SimpleComponent.class);
    /**
     * Track Blighted curse status for players.
     */
    @SuppressWarnings("rawtypes")
    public static final ComponentKey<SimpleComponent> VIGOR = ComponentRegistry.getOrCreate(new Identifier(MODID, "vigor"),
            SimpleComponent.class);
    /**
     * Makes Warped arrows teleport owner
     */
    public static final ComponentKey<BaseComponent> WARP = ComponentRegistry.getOrCreate(new Identifier(MODID, "warp"),
            BaseComponent.class);

    /**
     * These components are ticked during certain tick events for living entities.
     */
    public static final List<ComponentKey<BaseComponent>> LIVING_ENTITY_TICKING_COMPONENTS = List.of(
            ADAPTABILITY,
            ASSAULT_DASH,
            BURST_FIRE,
            HAUNTED,
            MOODY
    );
    /**
     * These components are called during certain events for projectiles.
     */
    public static final List<ComponentKey<BaseComponent>> PROJECTILE_COMPONENTS = List.of(
            IGNORES_IFRAMES,
            LIGHTNING_BOLT,
            WARP,
            MARKED,
            OVERSIZED,
            HITSCAN,
            GRAPPLING
    );

    @SuppressWarnings("rawtypes")
    public static final List<ComponentKey<SimpleComponent>> RESET_ON_DEATH = List.of(
            BLIGHTED,
            VIGOR
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ADAPTABILITY, e -> new EnchantmentComponent("adaptability") {
            @Override
            public void onTick(LivingEntity entity) {
                if (this.getValue() > 0 && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ADAPTABILITY, entity) > 0) {
                    this.decrement();
                }
            }
        });
        registry.registerFor(PlayerEntity.class, ASSAULT_DASH, e -> new EnchantmentComponent("assault_dash") {
            private Vec3d dashVelocity;
            @Override
            public void setValue(int level, Entity user) {
                // set rem. assault dash ticks to 10, and set an intial assault dash velocity based on level
                this.setValue(10);
                dashVelocity = user.getRotationVecClient().multiply(1, 0, 1).normalize().multiply(0.6 + level * 0.3);
            }

            @Override
            public Vec3d onEntityTravel(Entity entity, int level, Vec3d velocity) {
                if (this.getValue() > 0 && entity instanceof PlayerEntity user) {
                    Vec3d userVelocity = dashVelocity;
                    if (userVelocity == null) {
                        this.setValue(0);
                        return new Vec3d(0, 0, 0);
                    }
                    Item item = user.getActiveItem().getItem();
                    // enter/continue assault dash state if entity is holding up a shield
                    if (item.getMaxUseTime(user.getActiveItem()) - user.getItemUseTimeLeft() >= 0 && user.isUsingItem() &&
                            item.getUseAction(user.getActiveItem()) == UseAction.BLOCK) {
                        // get a list of entity collisions in the current dash range
                        List<EntityHitResult> entityHitResults = EntityHelper.getEntityCollisions(user.getWorld(), user, user.getPos().subtract(this.dashVelocity),
                                user.getPos().add(this.dashVelocity), user.getBoundingBox().stretch(this.dashVelocity).expand(1.0),
                                e -> true, 0.5f);
                        for (EntityHitResult entityHitResult : entityHitResults) {
                            Entity target = entityHitResult.getEntity();
                            // damage target based on current velocity
                            if (target.damage(EnchantHelper.assaultDash(user), (float) this.dashVelocity.length() * 2)) {
                                // if damaged successfully, apply knockback and damage the shield
                                target.setVelocity(target.getVelocity().add(this.dashVelocity.multiply(1.2)).add(0, 0.2, 0));
                                if (target instanceof PlayerEntity) {
                                    target.velocityModified = true;
                                }
                                if (user instanceof ServerPlayerEntity) {
                                    if (!user.getAbilities().creativeMode) {
                                        user.getActiveItem().damage(1, user.getRandom(), (ServerPlayerEntity) user);
                                    }
                                    user.getWorld().playSoundFromEntity(null, target, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS,
                                            1.0f, user.getWorld().random.nextFloat() * 0.4f);
                                }
                            }
                        }
                        // decelerate by 25% per tick if at least 3 ticks have passed
                        if (this.getValue() < 8) {
                            this.dashVelocity = this.dashVelocity.multiply(0.75);
                        }
                        this.decrement();
                    }
                    // if user is not holding up a shield, stop assault dash immediately and decrease velocity
                    else {
                        this.setValue(0);
                        userVelocity = userVelocity.multiply(0.3);
                    }
                    velocity = userVelocity;
                }
                return velocity;
            }
        });
        registry.registerFor(PlayerEntity.class, BLIGHTED, e -> new SimpleEntityComponent<>("blighted", false));
        registry.registerFor(LivingEntity.class, BURST_FIRE, e -> new EnchantmentComponent("burst_fire") {
            private ItemStack itemStack = ItemStack.EMPTY;
            private Hand hand = null;
            private ItemStack storedProjectile = ItemStack.EMPTY;

            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                // store hand, crossbow, and projectile item data, set a burst timer, and add a cooldown, only if
                // initiating a burst; decrease projectile damage/velocity
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity && user.isAlive()) {
                    if (this.getValue() == 0) {
                        this.itemStack = item;
                        this.hand = user.getActiveHand();
                        this.storedProjectile = projectileItem;
                        this.setValue(11);
                        if (user instanceof PlayerEntity player) {
                            player.getItemCooldownManager().set(item.getItem(), 30);
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() - 0.3);
                        }
                    }
                    SupplementaryComponents.IGNORES_IFRAMES.get(projectileEntity).setValue(1);
                }
            }

            @Override
            public void onTick(LivingEntity livingEntity) {
                // only do burst fire logic if there is a burst fire timer active
                if (this.getValue() > 0) {
                    // if the active item matches the item used to initiate the burst fire, tick the burst timer down
                    // and fire an arrow every fourth tick
                    if (this.hand != null && livingEntity.getStackInHand(this.hand) == this.itemStack) {
                        if (this.getValue() % 4 == 0) {
                            float pitch = CrossbowItem.getSoundPitch(true, livingEntity.getRandom());
                            // basically conditional to make sure pillagers don't crash the game with burst fire
                            if (livingEntity instanceof CrossbowUser crossbowUser && crossbowUser.getTarget() != null ||
                                    !(livingEntity instanceof CrossbowUser)) {
                                CrossbowItem.shoot(livingEntity.getWorld(), livingEntity, hand, this.itemStack, this.storedProjectile,
                                        pitch, true,
                                        CrossbowItem.getSpeed(this.storedProjectile), livingEntity.isSneaking() ? 3.0F : 6.0F,
                                        0.0F);
                            }
                        }
                        this.setValue(this.getValue() - 1);
                    }
                    // if item does not match, cancel burst
                    else {
                        this.setValue(0);
                        this.hand = null;
                    }
                }
            }
        });
        registry.registerFor(ArrowEntity.class, ENHANCED, e -> new EnchantmentComponent("enhanced"));
        registry.registerFor(PersistentProjectileEntity.class, GRAPPLING, e -> new SyncedEnchantmentComponent("grappling") {
            // unused, for separating bow/crossbow impl later
            @SuppressWarnings("FieldCanBeLocal")
            private Hand activeHand = Hand.MAIN_HAND;
            private ItemStack activeStack;

            @Override
            public void setValue(int level, Entity user) {
                this.setValue(level);
                // store grappling settings
                if (user instanceof LivingEntity livingEntity) {
                    this.activeHand = livingEntity.getActiveHand();
                    this.activeStack = livingEntity.getStackInHand(this.activeHand);
                }
            }

            @Override
            public void inBlockTick(ProjectileEntity projectileEntity, int level) {
                if (!projectileEntity.getWorld().isClient()) {
                    Entity owner = projectileEntity.getOwner();
                    // proceed if owner exists, is holding the correct grappling item, and is within 50 blocks
                    if (owner instanceof LivingEntity livingEntity && owner.isAlive() && owner.getWorld() == projectileEntity
                            .getWorld() && livingEntity.getStackInHand(this.activeHand) == this.activeStack && projectileEntity
                            .getOwner().getPos().subtract(projectileEntity.getPos()).lengthSquared() < 2500 && GRAPPLING
                            .get(owner).getEntity() == projectileEntity) {
                        // pull user in, discard when player gets too close
                        double pullSpeed = Math.min(1, 1 / owner.getVelocity().lengthSquared()) * (owner.isTouchingWater() ? 0.05 : 0.2);
                        owner.setVelocity(projectileEntity.getPos().subtract(owner.getPos()).normalize().multiply(pullSpeed)
                                .add(owner.getVelocity()));
                        owner.velocityModified = true;
                        if (projectileEntity.getOwner().getPos().subtract(projectileEntity.getPos()).lengthSquared() < 2) {
                            this.setValue(0);
                            GRAPPLING.sync(projectileEntity);
                        }
                    }
                    // break the line if the grapple state is invalid
                    else {
                        this.setValue(0);
                        GRAPPLING.sync(projectileEntity);
                    }
                }
            }

            @Override
            public Vec3d onEntityTravel(Entity persistentProjectileEntity, int level, Vec3d velocity) {
                // allow arrow to ignore culling so line is always displayed
                if (!persistentProjectileEntity.getWorld().isClient() && !persistentProjectileEntity.ignoreCameraFrustum) {
                    GRAPPLING.sync(persistentProjectileEntity);
                }
                persistentProjectileEntity.ignoreCameraFrustum = true;
                return velocity;
            }

            @Override
            public void onProjectileRender(ProjectileEntityRenderer<PersistentProjectileEntity> renderer, ProjectileEntity projectileEntity,
                                           float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                           int[] rgbl, int level) {
                // renders a line behind grappling arrows
                Entity owner = projectileEntity.getOwner();
                if (GRAPPLING.get(projectileEntity).getValue() > 0 && owner != null) {
                    RenderHelper.ropeRender(projectileEntity, tickDelta, matrixStack, vertexConsumerProvider, owner, this.activeHand);
                }
                else {
                    this.setValue(0);
                    GRAPPLING.sync(projectileEntity);
                }
            }
        });
        registry.registerFor(FishingBobberEntity.class, GRAPPLING, e -> new EnchantmentComponent("grappling") {
            @Override
            public void inBlockTick(ProjectileEntity projectileEntity, int level) {
                // sets in-block state of the bobber, need weird velocity hack to have it stay in place
                this.setValue(2);
                projectileEntity.setVelocity(new Vec3d(0.0, 0.03, 0.0));
                projectileEntity.setYaw(0);
                projectileEntity.setPitch(0);
                projectileEntity.velocityModified = true;
            }

            @Override
            public void onTick(LivingEntity livingEntity) {
                // basically removes in-block state if bobber begins falling again
                this.setValue(1);
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // pulls user in on reel
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
                // remove grappling from existing projectiles if it exists on hand swing
                if (!entity.getWorld().isClient() && this.getEntity() instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    GRAPPLING.get(persistentProjectileEntity).setValue(0);
                    GRAPPLING.sync(persistentProjectileEntity);
                    this.setEntity(null);
                }
            }

            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                // lots of server-client sync stuff so grappling line can properly render
                // also resets prev. grapple if it exists
                if (projectileEntity instanceof PersistentProjectileEntity && !projectileEntity.getWorld().isClient()) {
                    SupplementaryComponents.GRAPPLING.get(projectileEntity).setValue(1, user);
                    Entity lastProjectile;
                    if ((lastProjectile = GRAPPLING.get(user).getEntity()) != null && lastProjectile instanceof PersistentProjectileEntity) {
                        GRAPPLING.get(lastProjectile).setValue(0);
                        GRAPPLING.sync(lastProjectile);
                    }
                    GRAPPLING.get(user).setEntity(projectileEntity);
                    GRAPPLING.sync(projectileEntity);
                }
                else if (projectileEntity instanceof FishingBobberEntity) {
                    GRAPPLING.get(projectileEntity).setValue(1);
                }
            }
        });
        registry.registerFor(PhantomEntity.class, HAUNTED, e -> new EnchantmentComponent("haunted") {
            @Override
            public void onTick(LivingEntity entity) {
                if (this.getValue() > 0) {
                    this.increment();
                    if (this.getValue() > 2400 && this.getValue() % 40 == 0) {
                        entity.damage(entity.getWorld().getDamageSources().magic(), 5);
                    }
                }
            }
        });
        registry.registerFor(PlayerEntity.class, HAUNTED, e -> new EnchantmentComponent("haunted") {
            @Override
            public void onTick(LivingEntity entity) {
                // logic mostly copied from PhantomSpawner class
                World w = entity.getWorld();
                if (w instanceof ServerWorld world && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.HAUNTED,
                        entity) > 0) {
                    this.decrement();
                    Random random = world.getRandom();
                    if (this.getValue() <= 0) {
                        this.setValue(1200 + random.nextInt(1200));
                        BlockPos blockPos;
                        LocalDifficulty localDifficulty;
                        BlockPos blockPos2;
                        if (entity instanceof ServerPlayerEntity serverPlayerEntity && !serverPlayerEntity.isSpectator() &&
                                (localDifficulty = world.getLocalDifficulty(blockPos = entity.getBlockPos())).isHarderThan(random.nextFloat() * 3.0f)) {
                            ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
                            int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)),
                                    288000, Integer.MAX_VALUE);
                            if (!(random.nextInt(j) < 72000 || !SpawnHelper.isClearForSpawn(world, blockPos2 = blockPos
                                            .up(20 + random.nextInt(15)).east(-10 + random.nextInt(21))
                                            .south(-10 + random.nextInt(21)), world.getBlockState(blockPos2),
                                    world.getFluidState(blockPos2), EntityType.PHANTOM))) {
                                int l = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);
                                for (int m = 0; m < l; ++m) {
                                    PhantomEntity phantomEntity = EntityType.PHANTOM.create(world);
                                    if (phantomEntity == null) continue;
                                    phantomEntity.refreshPositionAndAngles(blockPos2, 0.0f, 0.0f);
                                    phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, null, null);
                                    SupplementaryComponents.HAUNTED.maybeGet(phantomEntity).ifPresent(component -> component.setValue(1));
                                    phantomEntity.setTarget(serverPlayerEntity);
                                    world.spawnEntityAndPassengers(phantomEntity);
                                }
                            }
                        }
                    }
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, HITSCAN, e -> new EnchantmentComponent("hitscan") {
            @Override
            public void afterProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                // instantly do 50 iterations of main projectile tick loop
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity && (persistentProjectileEntity.isCritical() || persistentProjectileEntity instanceof TridentEntity)) {
                    int i;
                    List<Vector3f> path = new ArrayList<>();
                    List<PlayerEntity> players = new ArrayList<>();
                    for (i = 0; i < 65; i++) {
                        persistentProjectileEntity.tick();
                        path.add(persistentProjectileEntity.getPos().toVector3f());
                        // build list of nearby players to send particle packets to
                        if ((i & 0b111) == 0) {
                            projectileEntity.getWorld().getPlayers().forEach(player -> {
                                if (player.getPos().squaredDistanceTo(persistentProjectileEntity.getPos()) <= 128 * 128) {
                                    players.add(player);
                                }
                            });
                        }
                        // stop if unable to continue
                        if (persistentProjectileEntity.isRemoved()) break;
                        if (persistentProjectileEntity.inGround) break;
                    }
                    if (i != 0) {
                        SupplementaryPacketRegistry.HitscanPacket packet = new SupplementaryPacketRegistry.HitscanPacket(path);
                        players.forEach(player -> {
                            if (player instanceof ServerPlayerEntity p) {
                                packet.send(p);
                            }
                        });
                    }
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, IGNORES_IFRAMES, e -> new EnchantmentComponent("ignores_iframes") {
            @Override
            public void preEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // remove existing iframes on entity hit, before damage is applied
                if (target instanceof LivingEntity livingEntity) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 0;
                }
                else if (target instanceof EnderDragonPart dragonPart) {
                    dragonPart.owner.hurtTime = 0;
                    dragonPart.owner.timeUntilRegen = 0;
                }
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new EnchantmentComponent("lightning_bolt") {
            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // summon lightning if entity pos has skylight; resets target iframes so lightning actually does damage
                if (target instanceof LivingEntity livingEntity && target.getWorld().isSkyVisible(target.getBlockPos())) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 1;
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                    lightning.setPosition(target.getPos());
                    target.getWorld().spawnEntity(lightning);
                }
                // do not cancel after callback(s) finish
                return false;
            }

            @Override
            public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity projectileEntity, int lvl) {
                // summon lightning if block has skylight
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
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                // set tracking ticks to 10 and set the marked entity of this projectile to the user's stored target
                this.setValue(level * 10);
                Entity target = MARKED.get(user).getEntity();
                if (target instanceof LivingEntity || target instanceof EnderDragonPart) {
                    this.setEntity(target);
                }
            }

            @Override
            public Vec3d onEntityTravel(Entity entity, int level, Vec3d velocity) {
                // proceed if arrow is critical and has an owner
                if (entity instanceof PersistentProjectileEntity projectile &&
                        projectile.getOwner() instanceof LivingEntity owner && projectile.isCritical()) {
                    Entity target = MARKED.get(owner).getEntity();
                    if (target != null && target.getWorld() == entity.getWorld() && target.isAlive() && target.getPos().squaredDistanceTo(entity.getPos()) <= 4900) {
                        Vec3d projectilePos = entity.getPos();
                        Vec3d targetPos = target.getEyePos();
                        Vec3d projectileToTarget = new Vec3d(targetPos.x - projectilePos.x, targetPos.y -
                                projectilePos.y, targetPos.z - projectilePos.z);
                        Vec3d normVelocity = velocity.normalize();
                        // calculate angle between arrow and tracked mob, adjust course up to pi/8 radians per tick;
                        // correct projectile course by this amount
                        Vec3d normal = normVelocity.crossProduct(projectileToTarget).crossProduct(normVelocity).normalize();
                        double angle = Math.asin(Math.sqrt(normVelocity.crossProduct(projectileToTarget).lengthSquared() /
                                        (normVelocity.lengthSquared() * projectileToTarget.lengthSquared())));
                        angle = Math.min(angle, Math.PI / 8);
                        this.decrement();
                        return velocity.multiply(Math.cos(angle)).add(normal.multiply(Math.sin(angle))).normalize()
                                .multiply(velocity.length());
                    }
                }
                return velocity;
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // if owner exists and target is trackable, set owner target to entity hit
                if (projectileEntity.getOwner() instanceof LivingEntity owner && target != owner && (target instanceof LivingEntity || target instanceof EnderDragonPart)) {
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
        registry.registerFor(PlayerEntity.class, MOODY, e -> new SyncedEnchantmentComponent("moody") {

            private int current = 0;
            private int next = 0;
            private long startTime = 0;
            private long endTime = 0;

            @Override
            public void onTick(LivingEntity entity) {
                if (entity.getWorld().getTime() >= this.endTime) {
                    this.startTime = this.endTime;
                    this.endTime = entity.getWorld().getTime() + 100 + entity.getWorld().getRandom().nextInt(100);
                    this.current = this.next;
                    this.next = entity.getWorld().getRandom().nextInt(301) - 150;
                    MOODY.sync(entity);
                }
            }

            @Override
            public void writeToNbt(@NotNull NbtCompound tag) {
                super.writeToNbt(tag);
                tag.putInt("current", this.current);
                tag.putInt("next", this.next);
                tag.putLong("timer", this.startTime);
                tag.putLong("timer_max", this.endTime);
            }

            @Override
            public void readFromNbt(@NotNull NbtCompound tag) {
                super.readFromNbt(tag);
                this.current = tag.getInt("current");
                this.next = tag.getInt("next");
                this.startTime = tag.getLong("timer");
                this.endTime = tag.getLong("timer_max");
            }

            @Override
            public Object getCustom() {
                return new float[]{this.current, this.next, this.startTime, this.endTime};
            }
        });
        registry.registerFor(PersistentProjectileEntity.class, OVERSIZED, e -> new SyncedEnchantmentComponent("oversized") {
            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    // increase velocity and damage of fired arrow
                    this.setValue(level);
                    projectileEntity.setVelocity(projectileEntity.getVelocity().multiply(Math.pow(1.3, level)));
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + 0.5 * level);
                    OVERSIZED.sync(projectileEntity);
                }
            }

            @Override
            public void onProjectileRender(ProjectileEntityRenderer<PersistentProjectileEntity> renderer, ProjectileEntity projectileEntity,
                                           float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                           int[] rgbl, int level) {
                // scale arrow size up by (30 + 20 * level)%
                float scale = 1.3f + level * 0.2f;
                matrixStack.scale(scale, scale, scale);
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // ignore/disable shields on hit
                if (target instanceof PlayerEntity player && player.isBlocking()) {
                    ((LivingEntity) player).damageShield(2.0f);
                    player.disableShield(true);
                }
                return false;
            }
        });
        registry.registerFor(SnowGolemEntity.class, SNOWBALL_TYPE, e -> new SimpleEntityComponent<>("snowball_type", 0));
        registry.registerFor(LivingEntity.class, VIGOR, e -> new SimpleEntityComponent<>("vigor", false));
        registry.registerFor(PersistentProjectileEntity.class, WARP, e -> new SyncedEnchantmentComponent("warp") {
            @Override
            public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    // increase velocity and damage of fired arrow
                    this.setValue(level);
                    WARP.sync(projectileEntity);
                }
            }

            @Override
            public void onProjectileRender(ProjectileEntityRenderer<PersistentProjectileEntity> renderer, ProjectileEntity projectileEntity,
                                           float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                           int[] rgbl, int level) {
                // scale arrow size up by (30 + 20 * level)%
                rgbl[0] *= 0.7;
                rgbl[1] *= 0.1;
                rgbl[3] = 0xFFFFFF;
            }

            @Override
            public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
                // set min distance to target of 1
                if (target != null) {
                    Vec3d targetPos = target.getPos();
                    Vec3d tpPos = projectileEntity.getPos();
                    Vec3d dist = tpPos.subtract(targetPos);
                    if (dist.lengthSquared() > 1) {
                        projectileEntity.setPosition(targetPos.add(dist.normalize()));
                    }
                }
                // rest is just ender pearl teleport logic with enderman sfx
                for (int i = 0; i < 32; ++i) {
                    projectileEntity.getWorld().addParticle(ParticleTypes.PORTAL, projectileEntity.getX(), projectileEntity.getY() +
                                    projectileEntity.random.nextDouble() * 2.0, projectileEntity.getZ(), projectileEntity.random.nextGaussian(),
                            0.0, projectileEntity.random.nextGaussian());
                }
                if (!projectileEntity.getWorld().isClient) {
                    Entity entity = projectileEntity.getOwner();
                    if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                        if (serverPlayerEntity.networkHandler.isConnectionOpen() && serverPlayerEntity.getWorld() == projectileEntity.getWorld() &&
                                !serverPlayerEntity.isSleeping()) {
                            EndermiteEntity endermiteEntity;
                            if (projectileEntity.random.nextFloat() < 0.1f && projectileEntity.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) &&
                                    (endermiteEntity = EntityType.ENDERMITE.create(projectileEntity.getWorld())) != null) {
                                endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(),
                                        entity.getPitch());
                                projectileEntity.getWorld().spawnEntity(endermiteEntity);
                            }
                            projectileEntity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                                    projectileEntity.getSoundCategory(), 1.0f, 1.0f);
                            if (entity.hasVehicle()) {
                                serverPlayerEntity.requestTeleportAndDismount(projectileEntity.getX(), projectileEntity.getY(),
                                        projectileEntity.getZ());
                            } else {
                                entity.requestTeleport(projectileEntity.getX(), projectileEntity.getY(), projectileEntity.getZ());
                            }
                            projectileEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                            entity.onLanding();
                            entity.damage(projectileEntity.getDamageSources().fall(), 5.0f);
                        }
                    } else if (entity != null) {
                        projectileEntity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                                projectileEntity.getSoundCategory(), 1.0f, 1.0f);
                        entity.requestTeleport(projectileEntity.getX(), projectileEntity.getY(), projectileEntity.getZ());
                        projectileEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        entity.onLanding();
                    }
                }
                return false;
            }

            @Override
            public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity projectileEntity, int lvl) {
                if (blockHitResult != null) {
                    // center on hit blockface
                    projectileEntity.setPosition(blockHitResult.getBlockPos().add(blockHitResult.getSide().getVector()).toCenterPos());
                    this.postEntityHit(null, projectileEntity, lvl);
                }
            }
        });
    }
}
