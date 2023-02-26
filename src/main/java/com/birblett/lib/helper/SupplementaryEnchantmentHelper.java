package com.birblett.lib.helper;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.item.ItemStack;

public class SupplementaryEnchantmentHelper {

    public static float getOversizedDrawspeedModifier(float pullProgress, ItemStack stack) {
        return (float) (pullProgress * Math.pow(0.75, net.minecraft.enchantment.EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack)));
    }
}
