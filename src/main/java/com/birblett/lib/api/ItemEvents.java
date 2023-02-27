package com.birblett.lib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemEvents {

    public static final Event<ProjectileFiredEvent> ARROW_PROJECTILE_FIRED = EventFactory.createArrayBacked(ProjectileFiredEvent.class,
            callbacks -> (user, persistentProjectileEntity, itemStack) -> {
        for (ProjectileFiredEvent callback : callbacks) {
            callback.onProjectileFire(user, persistentProjectileEntity, itemStack);
        }
    });

    public static final Event<ProjectileFiredEvent> FISHING_ROD_USE = EventFactory.createArrayBacked(ProjectileFiredEvent.class,
            callbacks -> (user, persistentProjectileEntity, itemStack) -> {
                for (ProjectileFiredEvent callback : callbacks) {
                    callback.onProjectileFire(user, persistentProjectileEntity, itemStack);
                }
            });

    public static final Event<CrossbowPrefireEvent> CROSSBOW_PREFIRE = EventFactory.createArrayBacked(CrossbowPrefireEvent.class,
            callbacks -> (user, crossbow, hand) -> {
        for (CrossbowPrefireEvent callback : callbacks) {
            callback.beforeCrossbowFire(user, crossbow, hand);
        }
    });

    public static final Event<ItemUseEvent> GENERIC_ITEM_USE = EventFactory.createArrayBacked(ItemUseEvent.class,
            callbacks -> (user, hand) -> {
        for (ItemUseEvent callback : callbacks) {
            callback.onItemUse(user, hand);
        }
    });

    @FunctionalInterface
    public interface CrossbowPrefireEvent {
        void beforeCrossbowFire(PlayerEntity user, ItemStack crossbow, Hand hand);
    }

    @FunctionalInterface
    public interface ProjectileFiredEvent {
        void onProjectileFire(LivingEntity user, ProjectileEntity persistentProjectileEntity, ItemStack itemStack);
    }

    @FunctionalInterface
    public interface ItemUseEvent {
        void onItemUse(PlayerEntity user, Hand hand);
    }
}
