package com.birblett.registry;

import com.birblett.items.BoomerangItem;
import com.birblett.lib.builders.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

public class SupplementaryEnchantments {
    /*
    Declarations and registry for enchantments.

    Equipment slot presets
        MAIN_HAND - mainhand
        BOTH_HANDS - mainhand and offhand

    Enchantments
        BURST_FIRE - for crossbows; fires a 3 round burst of slightly weakened arrows
        GRAPPLING - for fishing rods and crossbows; projectiles trail lines behind them, and will pull the user in
        FRANTIC - for swords; gain a speed boost on crit, and deal additional damage while speed is boosted
        FRENZY - for swords; take more damage, deal more melee damage as health gets lower
        LIGHTNING_BOLT - for bows; projectiles summon lightning
        MARKED - for crossbows; initial hit will "mark" a target; subsequent projectiles will home in on the target
        PICKUP - for boomerangs; unlocks the internal inventory of boomerangs, and can pick up items
     */

    public static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
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
    };
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final EnchantmentBuilder PICKUP = new EnchantmentBuilder("pickup", Enchantment.Rarity.UNCOMMON,
            null, BOTH_HANDS);

    public static void buildAndRegister() {
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50)
                .addComponents(SupplementaryComponents.BURST_FIRE_TIMER)
                .build();
        GRAPPLING.makeIncompatible(Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT)
                .setPower(20,50)
                .addComponents(SupplementaryComponents.GRAPPLING)
                .setTreasure(true)
                .addCompatibleItems(Items.CROSSBOW, Items.FISHING_ROD)
                .build();
        FRANTIC.makeIncompatible(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK)
                .setPower(15, 5, 25, 5)
                .setMaxLevel(3)
                .build();
        FRENZY.makeIncompatible(FRANTIC)
                .setPower(20, 10, 30, 10)
                .setMaxLevel(3)
                .build();
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER)
                .setPower(20, 50)
                .addComponents(SupplementaryComponents.LIGHTNING_BOLT)
                .build();
        MARKED.makeIncompatible(BURST_FIRE)
                .setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponents(SupplementaryComponents.MARKED_LEVEL)
                .build();
        PICKUP.setPower(10, 10, 20, 20)
                .setMaxLevel(3)
                .addCompatibleClasses(BoomerangItem.class)
                .build();
    }
}
