package com.birblett.api;

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

public class EntityEvents {

    // ticked events
    private static final Function<EntityTickEvent[], EntityTickEvent> ENTITY_TICK_EVENT = callbacks -> (entity) -> {
        for (EntityTickEvent callback : callbacks) {
            callback.onEntityTick(entity);
        }
    };
    private static final Function<EntityTravelEvent[], EntityTravelEvent> ENTITY_TRAVEL_EVENT = callbacks -> (entity, velocity) -> {
        for (EntityTravelEvent callback : callbacks) {
            velocity = callback.onTravelTick(entity, velocity);
        }
        return velocity;
    };
    public static final Event<EntityTickEvent> PROJECTILE_GENERIC_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);
    public static final Event<EntityTickEvent> PROJECTILE_IN_BLOCK_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);
    public static final Event<EntityTickEvent> POST_TICK = EventFactory.createArrayBacked(EntityTickEvent.class, ENTITY_TICK_EVENT);
    public static final Event<EntityTravelEvent> PROJECTILE_TRAVEL_TICK = EventFactory.createArrayBacked(EntityTravelEvent.class, ENTITY_TRAVEL_EVENT);

    // instance events
    public static final Event<EntityDamageEvent> MODIFY_DAMAGE_EVENT = EventFactory.createArrayBacked(EntityDamageEvent.class,
            callbacks -> (entity, source, amount) -> {
        float initialDamage = amount;
        for (EntityDamageEvent callback : callbacks) {
            initialDamage = callback.onDamage(entity, source, initialDamage);
        }
        return initialDamage - amount;
    });
    public static final Event<LivingEntityHandSwingEvent> SWING_HAND_EVENT = EventFactory.createArrayBacked(LivingEntityHandSwingEvent.class,
            callbacks -> (entity, hand) -> {
        for (LivingEntityHandSwingEvent callback : callbacks) {
            callback.onHandSwing(entity, hand);
        }
    });
    public static final Event<FishingBobberReelEvent> FISHING_BOBBER_REEL_EVENT = EventFactory.createArrayBacked(FishingBobberReelEvent.class,
            callbacks -> (fishingBobberEntity, target) -> {
        VoidEventReturnable value = VoidEventReturnable.NO_OP, temp;
        for (FishingBobberReelEvent callback : callbacks) {
            if ((temp = callback.onReel(fishingBobberEntity, target)) != VoidEventReturnable.NO_OP) {
                value = temp;
                if (value == VoidEventReturnable.RETURN_IMMEDIATELY) {
                    return value;
                }
            }
        }
        return value;
    });
    public static final Event<LivingEntityAttackEvent> LIVING_ENTITY_ATTACK_EVENT = EventFactory.createArrayBacked(LivingEntityAttackEvent.class,
            callbacks -> (self, target, amount, isCritical) -> {
        for (LivingEntityAttackEvent callback : callbacks) {
            amount = callback.onAttack(self, target, amount, isCritical);
        }
        return amount;
    });
    public static final Event<EntityHitEvent> ARROW_BLOCK_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class,
            callbacks -> (hitResult, attacker) -> {
        if (hitResult instanceof BlockHitResult) {
            for (EntityHitEvent callback : callbacks) {
                callback.onHitEvent(hitResult, attacker);
            }
        }
    });
    public static final Event<EntityHitEvent> ARROW_PRE_ENTITY_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class,
            callbacks -> (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult) {
            for (EntityHitEvent callback : callbacks) {
                callback.onHitEvent(hitResult, attacker);
            }
        }
    });
    public static final Event<EntityHitEvent> ARROW_POST_ENTITY_HIT_EVENT = EventFactory.createArrayBacked(EntityHitEvent.class,
            callbacks -> (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult) {
            for (EntityHitEvent callback : callbacks) {
                callback.onHitEvent(hitResult, attacker);
            }
        }
    });

    @FunctionalInterface
    public interface EntityDamageEvent {
        float onDamage(Entity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface EntityHitEvent {
        void onHitEvent(HitResult hitResult, Entity attacker);
    }

    @FunctionalInterface
    public interface EntityTickEvent {
        void onEntityTick(Entity entity);
    }

    @FunctionalInterface
    public interface EntityTravelEvent {
        Vec3d onTravelTick(Entity entity, Vec3d velocity);
    }

    @FunctionalInterface
    public interface FishingBobberReelEvent {
        VoidEventReturnable onReel(FishingBobberEntity bobber, Entity target);
    }

    @FunctionalInterface
    public interface LivingEntityAttackEvent {
        float onAttack(LivingEntity self, Entity target, float amount, boolean isCritical);
    }

    @FunctionalInterface
    public interface LivingEntityHandSwingEvent {
        void onHandSwing(LivingEntity entity, Hand hand);
    }
}
