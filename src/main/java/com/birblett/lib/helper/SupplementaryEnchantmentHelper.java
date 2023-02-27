package com.birblett.lib.helper;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.item.ItemStack;

public class SupplementaryEnchantmentHelper {

    public static float getDrawspeedModifier(float base, ItemStack stack) {
        float oversizedModifier = (float) Math.pow(0.75, net.minecraft.enchantment.EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack));
        return base * oversizedModifier;
    }
}
