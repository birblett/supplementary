package com.birblett.armor_materials.steel_plate;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class SteelPlateArmorMaterial implements ArmorMaterial {

    // TODO: change base durability values to account for durability formula
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    // TODO: fix protection values
    private static final int[] PROTECTION_VALUES = new int[] {1, 1, 1, 1};

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 5;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getEntitySlotId()];
    }

    // TODO: remove placeholder value for base enchantability
    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
    }

    // TODO: remove placeholder material for repair ingredient
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.BARRIER);
    }

    @Override
    public String getName() {
        return "steel_plate";
    }

    // TODO: remove placeholder value for toughness
    @Override
    public float getToughness() {
        return 6.0F;
    }

    // TODO: remove placeholder value for knockback res
    @Override
    public float getKnockbackResistance() {
        return 1.0F;
    }

}