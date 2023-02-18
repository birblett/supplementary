package com.birblett.registry;

import com.birblett.items.BoomerangItem;
import com.birblett.lib.builders.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

public class SupplementaryEnchantments {

    public static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final EnchantmentBuilder GRAPPLING = new EnchantmentBuilder("grappling", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.CROSSBOW, MAIN_HAND);
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.VERY_RARE,
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
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER)
                .setPower(20, 50)
                .addComponents(SupplementaryComponents.LIGHTNING_BOLT)
                .build();
        MARKED.makeIncompatible(BURST_FIRE)
                .setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponents(SupplementaryComponents.MARKED_LEVEL)
                .build();
        PICKUP.setPower(10, 100)
                .setMaxLevel(1)
                .addCompatibleClasses(BoomerangItem.class)
                .build();
    }
}
