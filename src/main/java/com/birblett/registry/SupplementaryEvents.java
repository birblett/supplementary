package com.birblett.registry;

import com.birblett.lib.api.EntityEvents;
import com.birblett.lib.api.ItemEvents;
import com.birblett.lib.api.EventReturnable;
import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.lib.components.BaseComponent;
import com.birblett.lib.helper.EntityHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplementaryEvents {

    // non-entity events
    public static final ItemEvents.ProjectileFiredEvent ARROW_FIRED_COMPONENT_PROCESSOR = (user, projectile, itemStack) -> {
        if (projectile instanceof PersistentProjectileEntity) {
            EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level));
                        }
                    } else {
                        enchantmentBuilder.onProjectileFire(user, projectile, level);
                    }
                }
            });
        }
    };

    public static final ItemEvents.ItemUseEvent CROSSBOW_PREFIRE_COMPONENT_PROCESSOR = (user, crossbow, hand) ->
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

    public static final ItemEvents.ProjectileFiredEvent BOBBER_CAST_COMPONENT_PROCESSOR = (user, projectile, itemStack) -> {
        if (projectile instanceof FishingBobberEntity) {
            EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
                if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                    if (enchantmentBuilder.hasComponent()) {
                        for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                            componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level));
                        }
                    } else {
                        enchantmentBuilder.onProjectileFire(user, projectile, level);
                    }
                }
            });
        }
    };

    public static final ItemEvents.ItemUseEvent ITEM_USE_COMPONENT_PROCESSOR = (user, stack, hand) ->
        EnchantmentHelper.get(stack).forEach((enchantment, level) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.hasComponent()) {
                    for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                        componentKey.maybeGet(user).ifPresent(component -> component.onUse(user, hand));
                    }
                }
                else {
                    enchantmentBuilder.onUse(user, hand);
                }
            }
        });

    public static final EntityEvents.EntityDamageEvent APPLY_ENCHANTMENTS_TO_DAMAGE = (entity, source, amount) -> {
        if (entity instanceof MobEntity mobEntity) {
            float final_amount = amount;
            List<ItemStack> items = new ArrayList<>();
            items.add(mobEntity.getMainHandStack());
            items.add(mobEntity.getOffHandStack());
            mobEntity.getArmorItems().forEach(items::add);
            for (ItemStack itemStack : items) {
                for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.get(itemStack).entrySet()) {
                    if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                        final_amount += enchantmentBuilder.onDamage(mobEntity, source, enchantmentEntry.getValue(), final_amount);
                    }
                }
            }
            amount = final_amount;
        }
        return amount;
    };

    public static final EntityEvents.EntityHitEvent ARROW_BLOCK_HIT_COMPONENT_PROCESSOR = (hitResult, attacker) -> {
        if (hitResult instanceof BlockHitResult blockHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).onBlockHit(blockHitResult, self, componentKey.get(self).getValue());
                }
            }
        }
    };

    public static final EntityEvents.EntityHitEvent ARROW_ENTITY_PREHIT_COMPONENT_PROCESSOR = (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult entityHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).preEntityHit(entityHitResult.getEntity(), self, componentKey.get(self).getValue());
                }
            }
        }
    };

    public static final EntityEvents.EntityHitEvent ARROW_ENTITY_POSTHIT_COMPONENT_PROCESSOR = (hitResult, attacker) -> {
        if (hitResult instanceof EntityHitResult entityHitResult && attacker instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).postEntityHit(entityHitResult.getEntity(), self, componentKey.get(self).getValue());
                }
            }
        }
    };

    public static final EntityEvents.FishingBobberReelEvent EMPTY_REEL_COMPONENT_PROCESSOR = (bobber, target) -> {
        boolean shouldReturn = false;
        if (target == null) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.isProvidedBy(bobber) && componentKey.get(bobber).getValue() > 0) {
                    shouldReturn = shouldReturn || componentKey.get(bobber).postEntityHit(target, bobber, 1);
                }
            }
        }
        return shouldReturn ? EventReturnable.RETURN_AFTER_FINISH : EventReturnable.NO_OP;
    };

    public static final EntityEvents.FishingBobberReelEvent ENTITY_REEL_COMPONENT_PROCESSOR = (bobber, target) -> {
        boolean shouldReturn = false;
        if (target != null) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.isProvidedBy(bobber) && componentKey.get(bobber).getValue() > 0) {
                    shouldReturn = shouldReturn || componentKey.get(bobber).postEntityHit(target, bobber, 1);
                }
            }
        }
        return shouldReturn ? EventReturnable.RETURN_AFTER_FINISH : EventReturnable.NO_OP;
    };

    public static final EntityEvents.LivingEntityHandSwingEvent GRAPPLING_HAND_SWING_EVENT = (entity, hand) -> {
        if (entity instanceof PlayerEntity) {
            if (SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).getValue() > 0 || SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).getEntity() != null) {
                SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).onHandSwingEvent(entity, hand);
            }
        }
    };

    public static final EntityEvents.LivingEntityAttackEvent PLAYER_ATTACK_EVENT = (self, target, amount, isCritical) -> {
        if (self instanceof PlayerEntity) {
            for (Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.get(self.getMainHandStack()).entrySet()) {
                if (enchantment.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                    amount += enchantmentBuilder.onAttack(self, target, enchantment.getValue(), isCritical, amount);
                }
            }
        }
        return amount;
    };

    public static final EntityEvents.EntityTickEvent ARROW_IN_BLOCK_TICK = (entity) -> {
        if (entity instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).inBlockTick(self, componentKey.get(self).getValue());
                }
            }
        }
    };

    public static final EntityEvents.EntityTravelEvent ARROW_TRAVEL_COMPONENT_PROCESSOR = (entity, velocity) -> {
        if (entity instanceof PersistentProjectileEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0) {
                    Vec3d newVelocity =  componentKey.get(self).onProjectileTravel(self, componentKey.get(self).getValue(), velocity);
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

    public static final EntityEvents.EntityTickEvent FISHING_BOBBER_GENERIC_TICK = (entity) -> {
        if (entity instanceof FishingBobberEntity self) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                    componentKey.get(self).onTick(self.getPlayerOwner());
                }
            }
        }
    };

    public static final EntityEvents.EntityTickEvent FISHING_BOBBER_IN_BLOCK_TICK = (entity) -> {
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

    public static final EntityEvents.EntityTickEvent LIVING_ENTITY_TICK_COMPONENT_PROCESSOR = (entity) -> {
        if (entity instanceof LivingEntity livingEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
                componentKey.get(livingEntity).onTick(livingEntity);
            }
        }
    };

    public static void register() {
        ItemEvents.ITEM_USE.register(ITEM_USE_COMPONENT_PROCESSOR);
        ItemEvents.CROSSBOW_PREFIRE.register(CROSSBOW_PREFIRE_COMPONENT_PROCESSOR);
        ItemEvents.ARROW_PROJECTILE_FIRED.register(ARROW_FIRED_COMPONENT_PROCESSOR);
        ItemEvents.FISHING_ROD_USE.register(BOBBER_CAST_COMPONENT_PROCESSOR);

        EntityEvents.ARROW_BLOCK_HIT_EVENT.register(ARROW_BLOCK_HIT_COMPONENT_PROCESSOR);
        EntityEvents.ARROW_POST_ENTITY_HIT_EVENT.register(ARROW_ENTITY_POSTHIT_COMPONENT_PROCESSOR);
        EntityEvents.ARROW_PRE_ENTITY_HIT_EVENT.register(ARROW_ENTITY_PREHIT_COMPONENT_PROCESSOR);
        EntityEvents.FISHING_BOBBER_REEL_EVENT.register(EMPTY_REEL_COMPONENT_PROCESSOR);
        EntityEvents.FISHING_BOBBER_REEL_EVENT.register(ENTITY_REEL_COMPONENT_PROCESSOR);
        EntityEvents.ADDITIVE_DAMAGE_EVENT.register(APPLY_ENCHANTMENTS_TO_DAMAGE);
        EntityEvents.LIVING_ENTITY_ATTACK_EVENT.register(PLAYER_ATTACK_EVENT);
        EntityEvents.SWING_HAND_EVENT.register(GRAPPLING_HAND_SWING_EVENT);

        EntityEvents.ENTITY_GENERIC_TICK.register(LIVING_ENTITY_TICK_COMPONENT_PROCESSOR);
        EntityEvents.PROJECTILE_GENERIC_TICK.register(FISHING_BOBBER_GENERIC_TICK);
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.register(ARROW_IN_BLOCK_TICK);
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.register(FISHING_BOBBER_IN_BLOCK_TICK);
        EntityEvents.PROJECTILE_TRAVEL_TICK.register(ARROW_TRAVEL_COMPONENT_PROCESSOR);
    }
}
