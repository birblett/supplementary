package com.birblett.registry;

import com.birblett.lib.builders.BuiltEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class SupplementaryEnchantments {

    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    public static final BuiltEnchantment BURST_FIRE = new BuiltEnchantment("burst_fire", Enchantment.Rarity.COMMON,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final BuiltEnchantment LIGHTNING_BOLT = new BuiltEnchantment("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final BuiltEnchantment MARKED = new BuiltEnchantment("marked", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);

    public static void buildEnchantments() {
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50);
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER)
                .setPower(20, 50);
        MARKED.makeIncompatible(BURST_FIRE)
                .setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponent(SupplementaryComponents.MARKED_LEVEL, SupplementaryComponents.ComponentType.ARROW);

    }

    public static void register() {
        buildEnchantments();
        BURST_FIRE.register();
        LIGHTNING_BOLT.register();
        MARKED.register();
    }
}
