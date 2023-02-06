package com.birblett.armor_materials.steel_plate;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class SteelPlateArmorMaterial implements ArmorMaterial {

    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {1, 1, 1, 1};

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 5;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        // placeholder for enchantability - 15 is leather enchantability
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        // placeholder material for repair
        return Ingredient.ofItems(Items.BARRIER);
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return "name";
    }

    @Override
    public float getToughness() {
        // TODO remove placeholder value for toughness
        return 6.0F;
    }

    @Override
    public float getKnockbackResistance() {
        // TODO remove placeholder value for knockback res
        return 10.0F;
    }
}