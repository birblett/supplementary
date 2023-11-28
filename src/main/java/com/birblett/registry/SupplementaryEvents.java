package com.birblett.registry;

import com.birblett.lib.api.EntityEvents;
import com.birblett.lib.api.ItemEvents;
import com.birblett.lib.api.EventReturnable;
import com.birblett.lib.creational.EnchantmentBuilder;
import com.birblett.lib.components.BaseComponent;
import com.birblett.lib.helper.EntityHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Various event callbacks for game events.
 */
public class SupplementaryEvents {

    /**
     * <hr><center><h1>Item use events</h1></center><hr>
     * <br><br>
     * Called on item use. Handles enchant use events. Usually not called if item has an existing {@link
     * net.minecraft.item.Item#use(World, PlayerEntity, Hand)} implementation.
     * @see ItemEvents#ITEM_USE
     */
    public static final ItemEvents.ItemUseEvent ITEM_USE_ENCHANT_EVENTS = (user, stack, hand) ->
        EnchantmentHelper.get(stack).forEach((enchantment, level) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.hasComponent()) {
                    for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                        componentKey.maybeGet(user).ifPresent(component -> component.onUse(user, hand));
                    }
                } else {
                    enchantmentBuilder.onUse(user, hand);
                }
            }
        });
    /**
     * Called before an arrow is fired from a crossbow. Applies enchantment effects to fired arrows.
     * @see ItemEvents#CROSSBOW_PREFIRE
     */
    public static final ItemEvents.ItemUseEvent CROSSBOW_PREFIRE_ENCHANT_EVENTS = (user, crossbow, hand) ->
        EnchantmentHelper.get(crossbow).forEach((enchantment, lvl) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                List<ItemStack> projectiles = CrossbowItem.getProjectiles(crossbow);
                if (enchantmentBuilder.hasComponent()) {
                    for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                        componentKey.maybeGet(user).ifPresent(component -> component.onCrossbowUse(crossbow, hand, projectiles.get(0)));
                    }
                }
                else {
                    enchantmentBuilder.onCrossbowUse(crossbow, hand, projectiles.get(0));
                }
            }
        });

    /**
     * <hr><center><h1>Projectile instantiation events</h1></center><hr>
     * <br><br>
     * Called when an arrow is fired from either a bow or crossbow.
     * @see ItemEvents#ARROW_FIRED_EVENT
     */
    public static final ItemEvents.ProjectileFiredEvent ARROW_FIRED_ENCHANT_EVENTS = (user, projectile, item, arrow) -> {
        if (projectile instanceof ArrowEntity) {
            EnchantmentHelper.get(item).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(user).ifPresent(component -> component.onProjectileFire(user, projectile, level, item, arrow));
                            componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level, item, arrow));
                        }
                    } else {
                        enchantmentBuilder.onProjectileFire(user, projectile, level, item);
                    }
                }
            });
            EnchantmentHelper.get(item).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(user).ifPresent(component -> component.afterProjectileFire(user, projectile, level, item, arrow));
                            componentKey.maybeGet(projectile).ifPresent(component -> component.afterProjectileFire(user, projectile, level, item, arrow));
                        }
                    }
                }
            });
        }
    };
    /**
     * Called when a fishing bobber is cast. Applies enchantment effects to bobbers.
     * @see ItemEvents#FISHING_ROD_USE
     */
    public static final ItemEvents.ProjectileFiredEvent BOBBER_CAST_ENCHANT_EVENTS = (user, projectile, item, arrow) -> {
        if (projectile instanceof FishingBobberEntity) {
            EnchantmentHelper.get(item).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level, item, arrow));
                        }
                    } else {
                        enchantmentBuilder.onProjectileFire(user, projectile, level, item);
                    }
                }
            });
        }
    };
    /**
     * Called when a trident is thrown. Applies enchantment effects to tridents.
     * @see ItemEvents#TRIDENT_THROW
     */
    public static final ItemEvents.ProjectileFiredEvent TRIDENT_THROW_ENCHANT_EVENTS = (user, projectile, item, trident) -> {
        if (projectile instanceof TridentEntity) {
            EnchantmentHelper.get(item).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level, item, trident));
                        }
                    } else {
                        enchantmentBuilder.onProjectileFire(user, projectile, level, item);
                    }
                }
            });
        }
    };

    /**
     * <hr><center><h1>Entity attack and damage events</h1></center><hr>
     * <br><br>
     * Called when a living entity is damaged. Applies a flat damage decrease based on enchants, before multipliers are
     * applied.
     * @see EntityEvents#LIVING_ENTITY_ADDITIVE_DAMAGE_EVENT
     */
    public static final EntityEvents.EntityDamageEvent LIVING_ENTITY_ADD_ENCHANT_DAMAGE_EVENTS = (entity, source, amount) -> {
        if (entity instanceof LivingEntity livingEntity) {
            MutableFloat final_amount = new MutableFloat(amount);
            List<ItemStack> items = new ArrayList<>();
            items.add(livingEntity.getMainHandStack());
            items.add(livingEntity.getOffHandStack());
            livingEntity.getArmorItems().forEach(items::add);
            for (ItemStack itemStack : items) {
                for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.get(itemStack).entrySet()) {
                    if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                        enchantmentBuilder.onDamage(livingEntity, itemStack, source, enchantmentEntry.getValue(), final_amount);
                    }
                }
            }
            float multiplier = 1.0f;
            for (ItemStack itemStack : items) {
                for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.get(itemStack).entrySet()) {
                    if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                        multiplier *= enchantmentBuilder.onDamageMultiplier(livingEntity, itemStack, source, enchantmentEntry.getValue(), final_amount);
                    }
                }
            }
            final_amount.setValue(final_amount.getValue() * multiplier);
            amount = final_amount.getValue();
        }
        return amount;
    };
    /**
     * Called when a living entity is damaged. Applies a flat damage decrease based on enchants, before multipliers are
     * applied.
     * @see EntityEvents#LIVING_ENTITY_DEATH_EVENT
     */
    @SuppressWarnings("unchecked")
    public static final EntityEvents.EntityDeathEvent LIVING_ENTITY_SIMPLE_COMPONENT_RESET = (entity, source) -> SupplementaryComponents.RESET_ON_DEATH
            .forEach(component -> component.maybeGet(entity).ifPresent(comp -> comp.setValue(comp.getDefaultValue())));
    /**
     * Called when an arrow hits a block. Applies enchantment effects.
     * @see EntityEvents#ARROW_BLOCK_HIT_EVENT
     */
    public static final EntityEvents.EntityHitEvent ARROW_BLOCK_HIT_ENCHANT_EVENTS = (hitResult, attacker) -> {
        if (hitResult instanceof BlockHitResult blockHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).onBlockHit(blockHitResult, self, componentKey.get(self).getValue());
                }
            }
        }
    };
    /**
     * Called when an arrow hits an entity, before damage is applied. Applies enchantment effects.
     * @see EntityEvents#ARROW_PRE_ENTITY_HIT_EVENT
     */
    public static final EntityEvents.EntityHitEvent ARROW_ENTITY_PREHIT_ENCHANT_EVENTS = (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult entityHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).preEntityHit(entityHitResult.getEntity(), self, componentKey.get(self).getValue());
                }
            }
        }
    };
    /**
     * Called when an arrow hits an entity, after damage is applied. Applies enchantment effects.
     * @see EntityEvents#ARROW_POST_ENTITY_HIT_EVENT
     */
    public static final EntityEvents.EntityHitEvent ARROW_ENTITY_POSTHIT_ENCHANT_EVENTS = (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult entityHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).postEntityHit(entityHitResult.getEntity(), self, componentKey.get(self).getValue());
                }
            }
        }
    };
    /**
     * Called when a player attempts to attack another entity. Applies flat enchantment damage modifiers to the final
     * damage.
     * @see EntityEvents.LivingEntityAttackEvent
     */
    public static final EntityEvents.LivingEntityAttackEvent PLAYER_ATTACK_ENCHANT_EVENTS = (self, target, amount, isCritical, isMaxCharge) -> {
        if (self instanceof PlayerEntity) {
            for (ItemStack stack : self.getArmorItems()) {
                for (Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.get(stack).entrySet()) {
                    if (enchantment.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                        amount += enchantmentBuilder.onAttack(self, target, enchantment.getValue(), isCritical, isMaxCharge, amount);
                    }
                }
            }
            for (Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.get(self.getMainHandStack()).entrySet()) {
                if (enchantment.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                    amount += enchantmentBuilder.onAttack(self, target, enchantment.getValue(), isCritical, isMaxCharge, amount);
                }
            }
        }
        return amount;
    };

    /**<hr><center><h1>Ticked Events</h1></center><hr>
     * <br><br>
     *
     * Called when an arrow is ticked while stuck in the ground. Applies effects of attached components.
     * @see EntityEvents#PROJECTILE_IN_BLOCK_TICK
     */
    public static final EntityEvents.EntityTickEvent ARROW_IN_BLOCK_COMPONENT_TICK = (entity) -> {
        if (entity instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).inBlockTick(self, componentKey.get(self).getValue());
                }
            }
        }
    };
    /**
     * Called when a fishing bobber is ticked while stuck in the ground. Applies effects of attached components.
     * @see EntityEvents#PROJECTILE_IN_BLOCK_TICK
     */
    public static final EntityEvents.EntityTickEvent FISHING_BOBBER_IN_BLOCK_COMPONENT_TICK = (entity) -> {
        if (entity instanceof FishingBobberEntity self) {
            if (EntityHelper.isTouchingBlock(self, 0.02)) {
                for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                    if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                        componentKey.get(self).inBlockTick(self, componentKey.get(self).getValue());
                    }
                }
            }
        }
    };
    /**
     * Called at the beginning of a fishing bobber's tick. Applies effects of attached components.
     * @see EntityEvents#PROJECTILE_GENERIC_TICK
     */
    public static final EntityEvents.EntityTickEvent FISHING_BOBBER_COMPONENT_TICK = (entity) -> {
        if (entity instanceof FishingBobberEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).onTick(self.getPlayerOwner());
                }
            }
        }
    };
    /**
     * Called at the beginning of a LivingEntity's tick. Applies effects of attached components.
     * @see EntityEvents#PROJECTILE_GENERIC_TICK
     */
    public static final EntityEvents.EntityTickEvent LIVING_ENTITY_COMPONENT_TICK = (entity) -> {
        if (entity instanceof LivingEntity livingEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.LIVING_ENTITY_TICKING_COMPONENTS) {
                if (componentKey.isProvidedBy(livingEntity)) {
                    componentKey.get(livingEntity).onTick(livingEntity);
                }
            }
        }
    };
    /**
     * Called while movement is being ticked on an arrow. Applies effects of attached components.
     * @see EntityEvents#PROJECTILE_TRAVEL_TICK
     */
    public static final EntityEvents.EntityTravelEvent ARROW_TRAVEL_COMPONENT_TICK = (entity, velocity) -> {
        if (entity instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    Vec3d newVelocity =  componentKey.get(self).onEntityTravel(self, componentKey.get(self).getValue(), velocity);
                    if (newVelocity != velocity) {
                        self.setVelocity(newVelocity);
                        self.velocityModified = true;
                        velocity = newVelocity;
                    }
                }
            }
        }
        return velocity;
    };
    /**
     * Called while movement is being ticked on a LivingEntity. Applies effects of attached components.
     * @see EntityEvents#LIVING_ENTITY_TRAVEL_TICK
     */
    public static final EntityEvents.EntityTravelEvent LIVING_ENTITY_TRAVEL_TICK = (entity, velocity) -> {
        if (entity instanceof LivingEntity livingEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.LIVING_ENTITY_TICKING_COMPONENTS) {
                if (componentKey.isProvidedBy(livingEntity)) {
                    velocity = componentKey.get(entity).onEntityTravel(livingEntity, 0, velocity);
                }
            }
        }
        return velocity;
    };

    /**<hr><center><h1>Misc. Events</h1></center><hr>
     * <br><br>
     *
     * Called when a fishing bobber is reeled. Applies enchantment effects.
     * @see EntityEvents#FISHING_BOBBER_REEL_EVENT
     */
    public static final EntityEvents.FishingBobberReelEvent FISHING_REEL_ENCHANT_EVENTS = (bobber, target) -> {
        boolean shouldReturn = false;
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            if (componentKey.isProvidedBy(bobber) && componentKey.get(bobber).getValue() > 0) {
                shouldReturn = shouldReturn || componentKey.get(bobber).postEntityHit(target, bobber, 1);
            }
        }
        return shouldReturn ? EventReturnable.RETURN_AFTER_FINISH : EventReturnable.NO_OP;
    };
    /**
     * Called when a LivingEntity starts a hand swing animation. Applies Grappling enchantment effect if applicable.
     * @see EntityEvents#SWING_HAND_EVENT
     */
    public static final EntityEvents.LivingEntityHandSwingEvent LIVING_ENTITY_GRAPPLING_HAND_SWING_EVENT = (entity, hand) -> {
        if (entity instanceof PlayerEntity) {
            if (SupplementaryComponents.GRAPPLING.get(entity).getValue() > 0 || SupplementaryComponents.GRAPPLING.get(entity).getEntity() != null) {
                SupplementaryComponents.GRAPPLING.get(entity).onHandSwingEvent(entity, hand);
            }
        }
    };

    /**
     * Called when a block is successfully mined by a player. Applies enchantment effects.
     * @see EntityEvents#POSTMINE_EVENT
     */
    public static final EntityEvents.BlockBreakEvent POSTMINE_ENCHANT_EVENTS = (world, state, pos, miner, item) -> {
        for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.get(item).entrySet()) {
            if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                enchantmentBuilder.onBlockBreak(world, state, pos, miner, item);
            }
        }
    };

    public static void register() {
        ItemEvents.ITEM_USE.register(ITEM_USE_ENCHANT_EVENTS);
        ItemEvents.CROSSBOW_PREFIRE.register(CROSSBOW_PREFIRE_ENCHANT_EVENTS);

        ItemEvents.ARROW_FIRED_EVENT.register(ARROW_FIRED_ENCHANT_EVENTS);
        ItemEvents.FISHING_ROD_USE.register(BOBBER_CAST_ENCHANT_EVENTS);
        ItemEvents.TRIDENT_THROW.register(TRIDENT_THROW_ENCHANT_EVENTS);

        EntityEvents.LIVING_ENTITY_ADDITIVE_DAMAGE_EVENT.register(LIVING_ENTITY_ADD_ENCHANT_DAMAGE_EVENTS);
        EntityEvents.LIVING_ENTITY_DEATH_EVENT.register(LIVING_ENTITY_SIMPLE_COMPONENT_RESET);
        EntityEvents.ARROW_BLOCK_HIT_EVENT.register(ARROW_BLOCK_HIT_ENCHANT_EVENTS);
        EntityEvents.ARROW_POST_ENTITY_HIT_EVENT.register(ARROW_ENTITY_POSTHIT_ENCHANT_EVENTS);
        EntityEvents.ARROW_PRE_ENTITY_HIT_EVENT.register(ARROW_ENTITY_PREHIT_ENCHANT_EVENTS);

        EntityEvents.PLAYER_ENTITY_ATTACK_EVENT.register(PLAYER_ATTACK_ENCHANT_EVENTS);
        EntityEvents.SWING_HAND_EVENT.register(LIVING_ENTITY_GRAPPLING_HAND_SWING_EVENT);

        EntityEvents.ENTITY_GENERIC_TICK.register(LIVING_ENTITY_COMPONENT_TICK);
        EntityEvents.LIVING_ENTITY_TRAVEL_TICK.register(LIVING_ENTITY_TRAVEL_TICK);
        EntityEvents.PROJECTILE_GENERIC_TICK.register(FISHING_BOBBER_COMPONENT_TICK);
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.register(ARROW_IN_BLOCK_COMPONENT_TICK);
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.register(FISHING_BOBBER_IN_BLOCK_COMPONENT_TICK);
        EntityEvents.PROJECTILE_TRAVEL_TICK.register(ARROW_TRAVEL_COMPONENT_TICK);

        EntityEvents.FISHING_BOBBER_REEL_EVENT.register(FISHING_REEL_ENCHANT_EVENTS);
        EntityEvents.POSTMINE_EVENT.register(POSTMINE_ENCHANT_EVENTS);
    }
}
