package com.birblett.lib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * Event interfaces and hooks for game events related to items.
 */
public class ItemEvents {

    /**
     * <hr><center><h1>Item use events</h1></center><hr>
     * These events hook into item use (normally when right click is pressed or released), and operate via side effects.
     */
    @FunctionalInterface
    public interface ItemUseEvent {
        void onItemUse(PlayerEntity user, ItemStack stack, Hand hand);
    }

    /**
     * Standard functional pattern for ItemUseEvent hooks.
     */
    private static final Function<ItemUseEvent[], ItemUseEvent> GENERIC_ITEM_USE = callbacks -> (user, stack, hand) -> {
        for (ItemUseEvent callback : callbacks) {
            callback.onItemUse(user, stack, hand);
        }
    };

    /**
     * Event hook for when any item is used. May not be called by items that override but do not include a super call to
     * {@link net.minecraft.item.Item#use(World, PlayerEntity, Hand)}.
     */
    public static final Event<ItemUseEvent> ITEM_USE = EventFactory.createArrayBacked(ItemUseEvent.class, GENERIC_ITEM_USE);
    /**
     * Event hook for when a crossbow is fired, before projectiles are instantiated.
     */
    public static final Event<ItemUseEvent> CROSSBOW_PREFIRE = EventFactory.createArrayBacked(ItemUseEvent.class, GENERIC_ITEM_USE);


    /**
     * <hr><center><h1>Item projectile instantiation events</h1></center><hr>
     * These events hook into projectile creation events, and allow modification of aforementioned projectile entities
     * via side-effects.
     */
    @FunctionalInterface
    public interface ProjectileFiredEvent {
        void onProjectileFire(LivingEntity user, ProjectileEntity persistentProjectileEntity, ItemStack itemStack);
    }

    /**
     * Standard functional pattern for ProjectileFiredEvent hooks.
     */
    private static final Function<ProjectileFiredEvent[], ProjectileFiredEvent> PROJECTILE_FIRED_EVENT = callbacks -> (user, projectileEntity, itemStack) -> {
        for (ProjectileFiredEvent callback : callbacks) {
            callback.onProjectileFire(user, projectileEntity, itemStack);
        }
    };

    /**
     * Event hook for when a bow or crossbow fires an arrow. May not be called by modded items that override but do not
     * include super calls to either
     * {@link net.minecraft.item.BowItem#onStoppedUsing(ItemStack, World, LivingEntity, int)} or
     * {@link net.minecraft.item.CrossbowItem#createArrow(World, LivingEntity, ItemStack, ItemStack)}
     */
    public static final Event<ProjectileFiredEvent> ARROW_FIRED_EVENT = EventFactory.createArrayBacked(ProjectileFiredEvent.class, PROJECTILE_FIRED_EVENT);
    /**
     * Event hook for when a fishing rod instantiates a fishing bobber entity.
     */
    public static final Event<ProjectileFiredEvent> FISHING_ROD_USE = EventFactory.createArrayBacked(ProjectileFiredEvent.class, PROJECTILE_FIRED_EVENT);
}
