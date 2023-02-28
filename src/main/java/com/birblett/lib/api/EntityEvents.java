package com.birblett.lib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

/**
 * Event interfaces and hooks for game events related to items.
 */
public class EntityEvents {

    /**
     * <hr><center><h1>Entity damage events</h1></center><hr>
     * These events hook into entity damage calculation, and return a modifier to be applied to the provided damage
     * amount. Provides additive and multiplicative modifier hooks, processed in that order.
     */
    @FunctionalInterface
    public interface EntityDamageEvent {
        float onDamage(Entity entity, DamageSource source, float amount);
    }

    /**
     * Standard functional patterns for EntityDamageEvent hooks.
     */
    private static final Function<EntityDamageEvent[], EntityDamageEvent> ENTITY_DAMAGE_EVENT_ADDITIVE = callbacks -> (entity, source, amount) -> {
        float initialDamage = amount;
        for (EntityDamageEvent callback : callbacks) {
            initialDamage = callback.onDamage(entity, source, initialDamage);
        }
        return initialDamage - amount;
    };
    private static final Function<EntityDamageEvent[], EntityDamageEvent> ENTITY_DAMAGE_EVENT_MULTIPLICATIVE = callbacks -> (entity, source, amount) -> {
        float multiplier = 1;
        for (EntityDamageEvent callback : callbacks) {
            multiplier *= callback.onDamage(entity, source, amount);
        }
        return multiplier;
    };

    /**
     * Event hook for when damage is dealt; provides a float value appended to the damage amount, with an initial value
     * of 0.0f.
     */
    public static final Event<EntityDamageEvent> ADDITIVE_DAMAGE_EVENT = EventFactory.createArrayBacked(EntityDamageEvent.class, ENTITY_DAMAGE_EVENT_ADDITIVE);
    /**
     * Event hook for when damage is dealt; provides a multiplier applied to the damage amount, with an initial value of
     * 1.
     */
    public static final Event<EntityDamageEvent> MULTIPLICATIVE_DAMAGE_EVENT = EventFactory.createArrayBacked(EntityDamageEvent.class, ENTITY_DAMAGE_EVENT_MULTIPLICATIVE);

    /**
     * <hr><center><h1>Entity tick events</h1></center><hr>
     * These events hook into entity ticks, typically at the beginning of the tick, and operate via side effects.
     */
    @FunctionalInterface
    public interface EntityTickEvent {
        void onEntityTick(Entity entity);
    }

    /**
     * Standard functional pattern for EntityTickEvent hooks.
     */
    private static final Function<EntityTickEvent[], EntityTickEvent> ENTITY_TICK_EVENT = callbacks -> (entity) -> {
        for (EntityTickEvent callback : callbacks) {
            callback.onEntityTick(entity);
        }
    };

    /**
     * Event hook for when a projectile entity is ticked.
     */
    public static final Event<EntityTickEvent> PROJECTILE_GENERIC_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);
    /**
     * Event hook for when a projectile entity is ticked while inside a block.
     */
    public static final Event<EntityTickEvent> PROJECTILE_IN_BLOCK_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);
    /**
     * Event hook for when an entity is ticked.
     */
    public static final Event<EntityTickEvent> ENTITY_GENERIC_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);

    /**
     * <hr><center><h1>Entity travel tick events</h1></center><hr>
     * These events hook into entity movement calculation, and return a modified velocity value.
     */
    @FunctionalInterface
    public interface EntityTravelEvent {
        Vec3d onTravelTick(Entity entity, Vec3d velocity);
    }

    /**
     * Standard functional pattern for EntityTravelEvent hooks.
     */
    private static final Function<EntityTravelEvent[], EntityTravelEvent> ENTITY_TRAVEL_EVENT = callbacks -> (entity, velocity) -> {
        for (EntityTravelEvent callback : callbacks) {
            velocity = callback.onTravelTick(entity, velocity);
        }
        return velocity;
    };

    /**
     * Event hook for when a projectile entity's movement is calculated. Expected to return some velocity value. This is
     * not applied as a modifier, but replaces the current velocity value.
     */
    public static final Event<EntityTravelEvent> PROJECTILE_TRAVEL_TICK = EventFactory.createArrayBacked(EntityTravelEvent.class, ENTITY_TRAVEL_EVENT);

    /**
     * <hr><center><h1>Entity hand swing events</h1></center><hr>
     * These events hook into hand swing animations, and operate via side effects. Does not include a standard
     * functional pattern, as this only applies to LivingEntity and its subclasses.
     */
    @FunctionalInterface
    public interface LivingEntityHandSwingEvent {
        void onHandSwing(LivingEntity entity, Hand hand);
    }

    /**
     * Event hook for when a LivingEntity swings a hand. This includes both the main- and off-hands, for any action that
     * results in a hand swing animation, like a fishing rod reel or an attack.
     */
    public static final Event<LivingEntityHandSwingEvent> SWING_HAND_EVENT = EventFactory.createArrayBacked(LivingEntityHandSwingEvent.class, callbacks -> (entity, hand) -> {
        for (LivingEntityHandSwingEvent callback : callbacks) {
            callback.onHandSwing(entity, hand);
        }
    });

    /**
     * <hr><center><h1>Fishing bobber reel events</h1></center><hr>
     * These events hook into the fishing bobber reel event. They operate via side effects, but return a value that
     * determines whether the hook should forcibly return after execution or not. Does not include a standard
     * functional pattern, as this only applies to FishingBobberEntity.
     *
     * @see EventReturnable
     */
    @FunctionalInterface
    public interface FishingBobberReelEvent {
        EventReturnable onReel(FishingBobberEntity bobber, Entity target);
    }

    /**
     * Event hook for when a fishing rod is reeled. Return value prioritizes {@link EventReturnable#RETURN_IMMEDIATELY},
     *  {@link EventReturnable#RETURN_AFTER_FINISH}, and {@link EventReturnable#NO_OP} in that order.
     */
    public static final Event<FishingBobberReelEvent> FISHING_BOBBER_REEL_EVENT = EventFactory.createArrayBacked(FishingBobberReelEvent.class,
            callbacks -> (fishingBobberEntity, target) -> {
                EventReturnable value = EventReturnable.NO_OP, temp;
                for (FishingBobberReelEvent callback : callbacks) {
                    if ((temp = callback.onReel(fishingBobberEntity, target)) != EventReturnable.NO_OP) {
                        value = temp;
                        if (value == EventReturnable.RETURN_IMMEDIATELY) {
                            return value;
                        }
                    }
                }
                return value;
            });

    /**
     * <hr><center><h1>Entity attack events</h1></center><hr>
     * These events hook into LivingEntities attempting to deal damage to another entity, and return a modified damage
     * amount.  Does not include a standard functional pattern, as this only applies to LivingEntity and its subclasses.
     */
    @FunctionalInterface
    public interface LivingEntityAttackEvent {
        float onAttack(LivingEntity self, Entity target, float amount, boolean isCritical);
    }

    /**
     * Event hook for when an entity damages another entity. Expected to return some damage value.
     */
    public static final Event<LivingEntityAttackEvent> LIVING_ENTITY_ATTACK_EVENT = EventFactory.createArrayBacked(LivingEntityAttackEvent.class,
            callbacks -> (self, target, amount, isCritical) -> {
                for (LivingEntityAttackEvent callback : callbacks) {
                    amount = callback.onAttack(self, target, amount, isCritical);
                }
                return amount;
            });

    /**
     * <hr><center><h1>Hit result processing events</h1></center><hr>
     * These events hook instances where a HitResult is being processed and operate via side effects. The standard
     * functional pattern provides separate implementations for BlockHitResults and EntityHitResults.
     */
    @FunctionalInterface
    public interface EntityHitEvent {
        void onHitEvent(HitResult hitResult, Entity attacker);
    }

    /**
     * Standard functional patterns for EntityHitEvent hooks.
     */
    private static final Function<EntityHitEvent[], EntityHitEvent> ON_BLOCK_HIT_EVENT = callbacks -> (hitResult, attacker) -> {
        if (hitResult instanceof BlockHitResult) {
            for (EntityHitEvent callback : callbacks) {
                callback.onHitEvent(hitResult, attacker);
            }
        }
    };
    private static final Function<EntityHitEvent[], EntityHitEvent> ON_ENTITY_HIT_EVENT = callbacks -> (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult) {
            for (EntityHitEvent callback : callbacks) {
                callback.onHitEvent(hitResult, attacker);
            }
        }
    };

    /**
     * Event hook when an arrow hits a block.
     */
    public static final Event<EntityHitEvent> ARROW_BLOCK_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class, ON_BLOCK_HIT_EVENT);
    /**
     * Event hook when an arrow hits an entity, before damage calculation.
     */
    public static final Event<EntityHitEvent> ARROW_PRE_ENTITY_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class, ON_ENTITY_HIT_EVENT);
    /**
     * Event hook when an arrow hits an entity, after damage calculation.
     */
    public static final Event<EntityHitEvent> ARROW_POST_ENTITY_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class, ON_ENTITY_HIT_EVENT);
}
