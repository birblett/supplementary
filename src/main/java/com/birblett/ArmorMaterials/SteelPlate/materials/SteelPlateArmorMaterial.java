package com.birblett.ArmorMaterials.SteelPlate.materials;

import com.birblett.registry.SupplementaryItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class CustomArmorMaterial implements ArmorMaterial {
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
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_X;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(.X);
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return "name";
    }

    @Override
    public float getToughness() {
        return X.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.XF;
    }
}