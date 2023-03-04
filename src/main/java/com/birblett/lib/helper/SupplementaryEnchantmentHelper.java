package com.birblett.lib.helper;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.item.ItemStack;

/**
 * Used to get certain attributes/values associated with enchantments. Will be used for serverconfig in the future.
 */
public class SupplementaryEnchantmentHelper {

    public static float getDrawspeedModifier(float base, ItemStack stack) {
        float oversizedModifier = (float) Math.pow(0.75, net.minecraft.enchantment.EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack));
        return base * oversizedModifier;
    }
}
