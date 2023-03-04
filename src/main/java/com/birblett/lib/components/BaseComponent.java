package com.birblett.lib.components;

import com.birblett.registry.SupplementaryEvents;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Base component interface used for easy storage of data and server-client sync. Provides a variety of implementable
 * events called via event  hook.
 * @see Component
 */
@SuppressWarnings("unused")
public interface BaseComponent extends Component {

    /**
     * Get stored value, if it exists.
     * @return stored value. Default: 0
     */
    default int getValue() {
        return 0;
    }

    /**
     * Set stored value, if it exists.
     */
    default void setValue(int level) {}

    /**
     * Set stored value, if it exists, and allows for operating via side effects with an entity provider.
     */
    default void setValue(int level, Entity provider) {}

    /**
     * Decrements stored value, if it exists.
     */
    default void decrement() {}

    /**
     * Increments stored value, if it exists.
     */
    default void increment() {}

    /**
     * Get stored entity, if it exists.
     * @return stored entity. Default: null
     */
    default Entity getEntity() {
        return null;
    }

    /**
     * Sets stored entity. Defaults to no-op.
     */
    default void setEntity(Entity entity) {}

    /**
     * Called when a projectile's hitbox is in contact with or intersects with a block.
     * @param projectileEntity colliding projectile entity
     * @param lvl provided enchantment level
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_IN_BLOCK_TICK
     * @see SupplementaryEvents#ARROW_IN_BLOCK_COMPONENT_TICK
     * @see SupplementaryEvents#FISHING_BOBBER_IN_BLOCK_COMPONENT_TICK
     */
    default void inBlockTick(ProjectileEntity projectileEntity, int lvl) {}

    /**
     * Called when a projectile collides with another entity, before damage and/or other on-collision effects are
     * applied.
     * @param target colliding entity
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     * @see com.birblett.lib.api.EntityEvents#ARROW_PRE_ENTITY_HIT_EVENT
     * @see SupplementaryEvents#ARROW_ENTITY_PREHIT_ENCHANT_EVENTS
     */
    default void preEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {}

    /**
     * Called when a projectile collides with another entity, after damage and/or other on-collision effects are
     * applied.
     * @param target colliding entity
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     * @return whether the method from which the event was initiated should return after all callbacks are finished
     * executing. Default: false
     * @see com.birblett.lib.api.EntityEvents#ARROW_POST_ENTITY_HIT_EVENT
     * @see com.birblett.lib.api.EntityEvents#FISHING_BOBBER_REEL_EVENT
     * @see SupplementaryEvents#ARROW_ENTITY_PREHIT_ENCHANT_EVENTS
     * @see SupplementaryEvents#EMPTY_REEL_ENCHANT_EVENTS
     * @see SupplementaryEvents#FISHING_REEL_ENCHANT_EVENTS
     */
    default boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
        return false;
    }

    /**
     * Called when a projectile collides with a block.
     * @param blockHitResult colliding block
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     * @see com.birblett.lib.api.EntityEvents#ARROW_BLOCK_HIT_EVENT
     * @see SupplementaryEvents#ARROW_BLOCK_HIT_ENCHANT_EVENTS
     */
    default void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity projectileEntity, int lvl) {}

    /**
     * Called during crossbow use, before projectile(s) creation.
     * @param stack crossbow ItemStack
     * @param hand active hand
     * @param savedProjectile projectile ItemStack
     * @see com.birblett.lib.api.ItemEvents#CROSSBOW_PREFIRE
     * @see SupplementaryEvents#CROSSBOW_PREFIRE_ENCHANT_EVENTS
     */
    default void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    /**
     * Called after projectile(s) is fired, before projectile(s) creation.
     * @param user entity from which projectile is being fired
     * @param projectileEntity  fired projectile entity
     * @param level provided enchantment level
     * @param item item projectile is being fired from
     * @param projectileItem item corresponding to the projectile
     * @see com.birblett.lib.api.ItemEvents#ARROW_FIRED_EVENT
     * @see com.birblett.lib.api.ItemEvents#FISHING_ROD_USE
     * @see SupplementaryEvents#ARROW_FIRED_ENCHANT_EVENTS
     */
    default void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {}

    /**
     * Called before rendering begins. Operates via side-effects, typically on the provided matrix stack.
     * @param projectileEntity rendered projectile entity
     * @param tickDelta subtick delta used for smooth rendering. Note that forced syncs such as
     *                  <code>entity.velocityModified = true</code> may produce graphical inconsistencies.
     * @param matrixStack provided matrix stack
     * @param vertexConsumerProvider provided vertex consumer
     * @param level provided enchantment level
     * @see com.birblett.mixin.render.ProjectileEntityRendererMixin#onRenderEvent(PersistentProjectileEntity, float, float, MatrixStack, VertexConsumerProvider, int, CallbackInfo)
     */
    default void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int level) {}

    /**
     * Called when an entity begins a hand swing animation. This encompasses both right and left click hand swings.
     * @param entity provided entity
     * @param hand active (swinging) hand
     * @see com.birblett.lib.api.EntityEvents#SWING_HAND_EVENT
     * @see SupplementaryEvents#LIVING_ENTITY_GRAPPLING_HAND_SWING_EVENT
     */
    default void onHandSwingEvent(LivingEntity entity, Hand hand) {}

    /**
     * Called when an entity uses (right clicks) with an item. Item classes with existing use() implementations may
     * override this.
     * @param entity provided entity
     * @param hand active hand
     * @see com.birblett.lib.api.ItemEvents#ITEM_USE
     * @see SupplementaryEvents#ITEM_USE_ENCHANT_EVENTS
     */
    default void onUse(LivingEntity entity, Hand hand) {}

    /**
     * Called every time an entity with the implementing component is ticked.
     * @param entity ticked entity
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_GENERIC_TICK
     * @see com.birblett.lib.api.EntityEvents#ENTITY_GENERIC_TICK
     * @see SupplementaryEvents#FISHING_BOBBER_COMPONENT_TICK
     * @see SupplementaryEvents#LIVING_ENTITY_COMPONENT_TICK
     */
    default void onTick(LivingEntity entity) {}

    /**
     * Called every tick, when projectile entity movement is being calculated.
     * @param projectileEntity ticked projectile entity
     * @param level provided enchantment level
     * @param velocity current projectile velocity
     * @return modified velocity following enchantment modifications/calculations. Default: current velocity
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_TRAVEL_TICK
     * @see SupplementaryEvents#ARROW_TRAVEL_COMPONENT_TICK
     */
    default Vec3d onEntityTravel(Entity projectileEntity, int level, Vec3d velocity) {
        return velocity;
    }
}