package com.birblett.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Hand;

public class EntityEvents {

    public static final Event<EntityTickEvent> POST_TICK = EventFactory.createArrayBacked(EntityTickEvent.class,
            callbacks -> (entity) -> {
        for (EntityTickEvent callback : callbacks) {
            callback.onEntityTick(entity);
        }
    });

    public static final Event<EntityDamageEvent> MODIFY_DAMAGE = EventFactory.createArrayBacked(EntityDamageEvent.class,
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

    @FunctionalInterface
    public interface EntityDamageEvent {
        float onDamage(Entity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface LivingEntityHandSwingEvent {
        void onHandSwing(LivingEntity entity, Hand hand);
    }

    @FunctionalInterface
    public interface EntityTickEvent {
        void onEntityTick(Entity entity);
    }
}
