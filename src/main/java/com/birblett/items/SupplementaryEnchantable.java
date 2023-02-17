package com.birblett.items;

import net.minecraft.enchantment.Enchantment;

import java.util.List;

public interface SupplementaryEnchantable {

    List<Class<? extends Enchantment>> getValidEnchantments();
}
