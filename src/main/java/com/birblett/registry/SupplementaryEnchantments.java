package com.birblett.registry;

import com.birblett.items.BoomerangItem;
import com.birblett.lib.builders.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

import java.util.List;

public class SupplementaryEnchantments {
    /*
    Declarations and registry for enchantments.

    Equipment slot presets
        MAIN_HAND - mainhand
        BOTH_HANDS - mainhand and offhand
        ALL_ARMOR - private, from Enchantments class; all armor slots
        NONE - no valid slots

    Standard enchantments
        BURST_FIRE - for crossbows; fires a 3 round burst of slightly weakened arrows
        EMPOWERED - for any tool; increases the effective level of existing enchantments by 1
        GRAPPLING - for fishing rods and crossbows; projectiles trail lines behind them, and will pull the user in
        FRANTIC - for swords; gain a speed boost on crit, and deal additional damage while speed is boosted
        FRENZY - for swords; take more damage, deal more melee damage as health gets lower
        LIGHTNING_BOLT - for bows; projectiles summon lightning
        MARKED - for crossbows; initial hit will "mark" a target; subsequent projectiles will home in on the target
        PICKUP - for boomerangs; unlocks the internal inventory of boomerangs, and can pick up items
        SLIMED - for boots; become bouncy and slippery and take less fall damage
        SOULBOUND - for any tool; item remains in inventory on death, at the cost of durability; does not stay if not enough durability
     */

    public static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final EquipmentSlot[] NONE = new EquipmentSlot[]{};

    public static final EnchantmentBuilder BOOSTING = new EnchantmentBuilder("boosting", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder BUNNYHOP = new EnchantmentBuilder("bunnyhop", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final EnchantmentBuilder EMPOWERED = new EnchantmentBuilder("empowered", Enchantment.Rarity.RARE,
            EnchantmentTarget.BREAKABLE, NONE);
    public static final EnchantmentBuilder GRAPPLING = new EnchantmentBuilder("grappling", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.CROSSBOW, MAIN_HAND);
    public static final EnchantmentBuilder FRANTIC = new EnchantmentBuilder("frantic", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, float damageAmount) {
            float modifier = 0.0f;
            if (user.hasStatusEffect(StatusEffects.SPEED)) {
                modifier = 0.1f * level * damageAmount;
            }
            if (isCritical) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 50));
            }
            return modifier;
        }
    };
    public static final EnchantmentBuilder FRENZY = new EnchantmentBuilder("frenzy", Enchantment.Rarity.RARE,
            EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, float damageAmount) {
            float lostHealthPercent = 2 * Math.max(0.5f, 1 - user.getHealth() / user.getMaxHealth());
            return lostHealthPercent * 0.2f * level * damageAmount;
        }

        @Override
        public float onDamage(LivingEntity user, DamageSource source, int level, float damageAmount) {
            return source.getAttacker() != null ? damageAmount * 0.2f : 0.0f;
        }
    };
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final EnchantmentBuilder PICKUP = new EnchantmentBuilder("pickup", Enchantment.Rarity.UNCOMMON,
            null, BOTH_HANDS);
    public static final EnchantmentBuilder SLIMED = new EnchantmentBuilder("slimed", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder SOULBOUND = new EnchantmentBuilder("soulbound", Enchantment.Rarity.RARE,
            EnchantmentTarget.BREAKABLE, NONE);
    public static final EnchantmentBuilder WATER_WALKING = new EnchantmentBuilder("water_walking", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);

    public static final EnchantmentBuilder[] MOBILITY_INCOMPATIBILITY_GROUP = {BOOSTING, BUNNYHOP, SLIMED, WATER_WALKING};

    public static void buildAndRegister() {
        // general enchants
        EMPOWERED.makeIncompatible(SOULBOUND)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
        SOULBOUND.makeIncompatible(EMPOWERED)
                .setPower(20, 50)
                .setTreasure(true)
                .build();

        // crossbow enchants
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.BURST_FIRE_TIMER)
                .build();
        GRAPPLING.makeIncompatible(Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT)
                .setPower(20,50)
                .addComponent(SupplementaryComponents.GRAPPLING)
                .setTreasure(true)
                .addCompatibleItems(Items.CROSSBOW, Items.FISHING_ROD)
                .build();
        MARKED.makeIncompatible(BURST_FIRE)
                .setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponent(SupplementaryComponents.MARKED_LEVEL)
                .build();

        // sword enchants
        FRANTIC.makeIncompatible(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK)
                .setPower(15, 5, 25, 5)
                .setMaxLevel(3)
                .build();
        FRENZY.makeIncompatible(FRANTIC)
                .setPower(20, 10, 30, 10)
                .setMaxLevel(3)
                .build();

        // bow enchants
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.LIGHTNING_BOLT)
                .build();

        // boomerang
        PICKUP.setPower(10, 10, 20, 20)
                .setMaxLevel(3)
                .addCompatibleClasses(BoomerangItem.class)
                .build();

        // mobility enchants
        BOOSTING.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(50, 50)
                .build();
        BUNNYHOP.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(50, 50)
                .build();
        SLIMED.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(50, 50)
                .build();
        WATER_WALKING.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(50, 50)
                .build();
    }
}
