package com.birblett.registry;

import com.birblett.api.EntityEvents;
import com.birblett.api.ItemEvents;
import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.lib.components.BaseComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;

public class SupplementaryEvents {

    public static final ItemEvents.CrossbowPrefireEvent APPLY_ENCHANTMENTS_TO_ARROW_ITEMSTACK = (user, crossbow, hand) -> {
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
    };
    public static final ItemEvents.ProjectileFiredEvent APPLY_ENCHANTMENTS_TO_PROJECTILE = (user, projectile, itemStack) -> {
        EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.hasComponent()) {
                    for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                        componentKey.maybeGet(projectile).ifPresent(component -> component.onProjectileFire(user, projectile, level));
                    }
                }
                else {
                    enchantmentBuilder.onProjectileFire(user, projectile, level);
                }
            }
        });
    };
    public static final ItemEvents.ItemUseEvent APPLY_ENCHANTMENTS_ON_ITEM_USE = (user, hand) -> {
        EnchantmentHelper.get(user.getStackInHand(hand)).forEach((enchantment, level) -> {
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
    };

    public static final EntityEvents.EntityTickEvent LIVING_ENTITY_TICK_COMPONENTS = (entity) -> {
        if (entity instanceof LivingEntity livingEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
                componentKey.get(livingEntity).onTick(livingEntity);
            }
        }
    };
    public static final EntityEvents.EntityDamageEvent APPLY_ENCHANTMENTS_TO_DAMAGE = (entity, source, amount) -> {
        float final_amount = amount;
        if (entity instanceof LivingEntity livingEntity) {
            DefaultedList<ItemStack> itemList = (DefaultedList<ItemStack>) livingEntity.getArmorItems();
            itemList.add(((LivingEntity) entity).getMainHandStack());
            itemList.add(((LivingEntity) entity).getOffHandStack());
            for (ItemStack itemStack : itemList) {
                for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.get(itemStack).entrySet()) {
                    if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                        final_amount += enchantmentBuilder.onDamage(livingEntity, source, enchantmentEntry.getValue(), final_amount);
                    }
                }
            }
            final_amount -= amount;
        }
        return final_amount;
    };
    public static final EntityEvents.LivingEntityHandSwingEvent GRAPPLING_HAND_SWING_EVENT = (entity, hand) -> {
        if (entity instanceof PlayerEntity) {
            if (SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).getValue() > 0 || SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).getEntity() != null) {
                SupplementaryComponents.GRAPPLING_TRACKING_COMPONENT.get(entity).onHandSwingEvent(entity, hand);
            }
        }
    };

    public static void register() {
        ItemEvents.CROSSBOW_PREFIRE.register(APPLY_ENCHANTMENTS_TO_ARROW_ITEMSTACK);
        ItemEvents.GENERIC_ITEM_USE.register(APPLY_ENCHANTMENTS_ON_ITEM_USE);
        ItemEvents.ON_PROJECTILE_FIRED.register(APPLY_ENCHANTMENTS_TO_PROJECTILE);

        EntityEvents.POST_TICK.register(LIVING_ENTITY_TICK_COMPONENTS);
        EntityEvents.MODIFY_DAMAGE.register(APPLY_ENCHANTMENTS_TO_DAMAGE);
        EntityEvents.SWING_HAND_EVENT.register(GRAPPLING_HAND_SWING_EVENT);
    }
}
