package com.birblett.lib.creational;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public class ToolMaterialBuilder {

    public static ToolMaterial build(int mineLevel, int durability, float mineSpeed, float attackDmg, int enchantable, Supplier<Ingredient> ingredient) {
        return new ToolMaterial() {
            private final int miningLevel = mineLevel;
            private final int baseDurability = durability;
            private final float miningSpeed = mineSpeed;
            private final float attackDamage = attackDmg;
            private final int enchantability = enchantable;
            private final Supplier<Ingredient> repairIngredients = ingredient;

            @Override
            public int getDurability() {
                return this.baseDurability;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return this.miningSpeed;
            }

            @Override
            public float getAttackDamage() {
                return this.attackDamage;
            }

            @Override
            public int getMiningLevel() {
                return this.miningLevel;
            }

            @Override
            public int getEnchantability() {
                return this.enchantability;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return this.repairIngredients.get();
            }
        };
    }
}
