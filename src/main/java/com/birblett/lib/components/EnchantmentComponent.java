package com.birblett.lib.components;

import com.birblett.registry.SupplementaryEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.birblett.Supplementary.MODID;

/**
 * An implementation of the Cardinal Components ComponentV3, primarily for use with enchantments that require the use of
 * persistent stored data. Implementable methods are called via mixin event hooks and are no-op by default, while the
 * only data written to NBT is a value associated with the initial enchantment level unless otherwise specified. In its
 * most basic implementation, it only stores enchantment levels in attached projectile entities. Individual components
 * are instantiated and registered in {@link com.birblett.registry.SupplementaryComponents}
 */
public class EnchantmentComponent implements BaseComponent {

    private final String id;
    private int enchantmentLevel = 0;

    /**
     * @param id the string id associated with the component being initialized; must be added to the fabric.mod.json as
     *           "supplementary:{@literal <id>}"
     */
    public EnchantmentComponent(String id) {
        this.id = MODID + ":" + id;
    }

    /**
     * Default implementation of {@link BaseComponent#getValue()}. Returns current stored enchantment level.
     *
     * @return stored enchantment level
     */
    @Override
    public int getValue() {
        return this.enchantmentLevel;
    }

    /**
     * Default implementation of {@link BaseComponent#setValue(int)}. Sets the stored enchantment level.
     *
     * @param level integer to set stored level to
     */
    @Override
    public void setValue(int level) {
        this.enchantmentLevel = level;
    }

    /**
     * Default implementation of {@link BaseComponent#decrement()}. Decrements the stored enchantment level by 1.
     */
    @Override
    public void decrement() {
        this.enchantmentLevel--;
    }

    /**
     * Default implementation of {@link BaseComponent#increment()}. Increments the stored enchantment level by 1.
     */
    @Override
    public void increment() {
        this.enchantmentLevel++;
    }

    /**
     * No-op implementation of {@link BaseComponent#getEntity()}. Defer to individual instances of this class to
     * implement this method.
     *
     * @return null
     */
    @Override @Nullable
    public Entity getEntity() {
        return null;
    }

    /**
     * No-op implementation of {@link BaseComponent#setEntity(Entity)}. Defer to individual instances of this class to
     * implement this method.
     */
    @Override
    public void setEntity(Entity entity) {}

    /**
     * No-op implementation of {@link BaseComponent#inBlockTick(ProjectileEntity, int)}. Called when a projectile's
     * hitbox is in contact with or intersects with a block. Implemented via event hook.
     *
     * @param projectileEntity colliding projectile entity
     * @param lvl provided enchantment level
     *
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_IN_BLOCK_TICK
     * @see SupplementaryEvents#ARROW_IN_BLOCK_TICK
     * @see SupplementaryEvents#FISHING_BOBBER_IN_BLOCK_TICK
     */
    @Override
    public void inBlockTick(ProjectileEntity projectileEntity, int lvl) {}

    /**
     * No-op implementation of {@link BaseComponent#preEntityHit(Entity, ProjectileEntity, int)}. Called when a
     * projectile collides with another entity, before damage and/or other on-collision effects are applied. Implemented
     * via event hook.
     *
     * @param target colliding entity
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     *
     * @see com.birblett.lib.api.EntityEvents#ARROW_PRE_ENTITY_HIT_EVENT
     * @see SupplementaryEvents#ARROW_ENTITY_PREHIT_COMPONENT_PROCESSOR
     */
    @Override
    public void preEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {}

    /**
     * No-op implementation of {@link BaseComponent#postEntityHit(Entity, ProjectileEntity, int)}. Called when a
     * projectile collides with another entity, after damage and/or other on-collision effects are applied. Implemented
     * via event hook.
     *
     * @param target colliding entity
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     *
     * @return whether the method from which the event was initiated should return after all callbacks are finished
     * executing. Default: false
     *
     * @see com.birblett.lib.api.EntityEvents#ARROW_POST_ENTITY_HIT_EVENT
     * @see com.birblett.lib.api.EntityEvents#FISHING_BOBBER_REEL_EVENT
     * @see SupplementaryEvents#ARROW_ENTITY_PREHIT_COMPONENT_PROCESSOR
     * @see SupplementaryEvents#EMPTY_REEL_COMPONENT_PROCESSOR
     * @see SupplementaryEvents#ENTITY_REEL_COMPONENT_PROCESSOR
     */
    @Override
    public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
        return false;
    }

    /**
     * No-op implementation of {@link BaseComponent#onBlockHit(BlockHitResult, ProjectileEntity, int)}. Called when a
     * projectile collides with a block. Implemented via event hook.
     *
     * @param blockHitResult colliding block
     * @param projectileEntity  colliding projectile entity
     * @param lvl provided enchantment level
     *
     * @see com.birblett.lib.api.EntityEvents#ARROW_BLOCK_HIT_EVENT
     * @see SupplementaryEvents#ARROW_BLOCK_HIT_COMPONENT_PROCESSOR
     */
    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity projectileEntity, int lvl) {}

    /**
     * No-op implementation of {@link BaseComponent#onCrossbowUse(ItemStack, Hand, ItemStack)}. Called during crossbow
     * use, before projectile(s) creation. Implemented via event hook.
     *
     * @param stack crossbow ItemStack
     * @param hand  active hand
     * @param savedProjectile projectile ItemStack
     *
     * @see com.birblett.lib.api.ItemEvents#CROSSBOW_PREFIRE
     * @see SupplementaryEvents#CROSSBOW_PREFIRE_COMPONENT_PROCESSOR
     */
    @Override
    public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    /**
     * Default implementation of {@link BaseComponent#onProjectileFire(LivingEntity, ProjectileEntity, int)}. Called
     * after projectile(s) is fired, before projectile(s) creation. By default, sets the provided projectile's stored
     * enchantment level to the provided level. Implemented via event hook.
     *
     * @param user entity from which projectile is being fired
     * @param projectileEntity  fired projectile entity
     * @param level provided enchantment level
     *
     * @see com.birblett.lib.api.ItemEvents#ARROW_PROJECTILE_FIRED
     * @see com.birblett.lib.api.ItemEvents#FISHING_ROD_USE
     * @see SupplementaryEvents#ARROW_FIRED_COMPONENT_PROCESSOR
     */
    @Override
    public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {
        this.setValue(level);
    }

    /**
     * No-op implementation of {@link  BaseComponent#onProjectileRender(ProjectileEntity, float, MatrixStack, VertexConsumerProvider, int)}.
     * Defer to individual instances of this class to implement this method. Called via mixin before rendering begins.
     * Operates via side-effects, typically on the provided matrix stack.
     *
     * @param projectileEntity rendered projectile entity
     * @param tickDelta subtick delta used for smooth rendering
     * @param matrixStack provided matrix stack
     * @param vertexConsumerProvider provided vertex consumer
     * @param level provided enchantment level
     *
     * @see com.birblett.mixin.render.ProjectileEntityRendererMixin#onRenderEvent(PersistentProjectileEntity, float, float, MatrixStack, VertexConsumerProvider, int, CallbackInfo)
     */
    @Override
    public void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack,
                                   VertexConsumerProvider vertexConsumerProvider, int level) {}

    /**
     * No-op implementation of {@link BaseComponent#onHandSwingEvent(LivingEntity, Hand)}. Called when an entity
     * initiates a hand swing animation. This encompasses both right and left click hand swings. Implemented via event
     * hook.
     *
     * @param entity provided entity
     * @param hand active (swinging) hand
     *
     * @see com.birblett.lib.api.EntityEvents#SWING_HAND_EVENT
     * @see SupplementaryEvents#GRAPPLING_HAND_SWING_EVENT
     */
    @Override
    public void onHandSwingEvent(LivingEntity entity, Hand hand) {}

    /**
     * No-op implementation of {@link BaseComponent#onUse(LivingEntity, Hand)}. Called when an entity uses (right
     * clicks) with an item. Implemented via event hook. Item classes with existing use() implementations may override
     * this.
     *
     * @param entity provided entity
     * @param hand active hand
     *
     * @see com.birblett.lib.api.ItemEvents#GENERIC_ITEM_USE
     * @see SupplementaryEvents#ITEM_USE_COMPONENT_PROCESSOR
     */
    @Override
    public void onUse(LivingEntity entity, Hand hand) {}

    /**
     * No-op implementation of {@link BaseComponent#onTick(LivingEntity)}. Called every tick. Implemented via event
     * hook.
     *
     * @param entity ticked entity
     *
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_GENERIC_TICK
     * @see com.birblett.lib.api.EntityEvents#ENTITY_GENERIC_TICK
     * @see SupplementaryEvents#FISHING_BOBBER_GENERIC_TICK
     * @see SupplementaryEvents#LIVING_ENTITY_TICK_COMPONENT_PROCESSOR
     */
    @Override
    public void onTick(LivingEntity entity) {}

    /**
     * Default implementation of {@link BaseComponent#onProjectileTravel(ProjectileEntity, int, Vec3d)}. Called every
     * tick, when projectile entity movement is being calculated. Return value replaces current projectile velocity.
     * Implemented via event hook.
     *
     * @param projectileEntity ticked projectile entity
     * @param level provided enchantment level
     * @param velocity current projectile velocity
     *
     * @return velocity following enchantment modifications/calculations. Default: current velocity
     *
     * @see com.birblett.lib.api.EntityEvents#PROJECTILE_TRAVEL_TICK
     * @see SupplementaryEvents#ARROW_TRAVEL_COMPONENT_PROCESSOR
     */
    @Override
    public Vec3d onProjectileTravel(ProjectileEntity projectileEntity, int level, Vec3d velocity) {
        return velocity;
    }

    /**
     * Default implementation of {@link dev.onyxstudios.cca.api.v3.component.Component#readFromNbt(NbtCompound)}.
     * Determines what data is read from NBT. By default, only reads the enchantment level.
     *
     * @param tag provided NBT object to read from
     */
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.enchantmentLevel = tag.getInt(this.id);
    }

    /**
     * Default implementation of {@link dev.onyxstudios.cca.api.v3.component.Component#writeToNbt(NbtCompound)}.
     * Determines what data is stored to NBT. By default, only stores the enchantment level.
     *
     * @param tag provided NBT object to write to
     */
    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(this.id, this.enchantmentLevel);
    }
}
