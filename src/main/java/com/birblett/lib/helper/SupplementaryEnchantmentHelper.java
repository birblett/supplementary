package com.birblett.lib.helper;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Used to get certain attributes/values associated with enchantments. Will be used for serverconfig in the future.
 */
public class SupplementaryEnchantmentHelper {

    /**
     * Apply draw speed modifiers for Growth and Oversized
     * @param base Base amount
     * @param stack Bow/crossbow itemstack
     * @return Draw speed after applying modifiers
     */
    public static float getDrawspeedModifier(float base, ItemStack stack) {
        float oversizedModifier = (float) Math.pow(0.75, net.minecraft.enchantment.EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack));
        float growthModifier = 1 + getGrowthStat(stack, GrowthKey.DRAW_SPEED);
        return base * oversizedModifier * growthModifier;
    }

    public static final String GROWTH_NBT_KEY = "SupplementaryGrowthStats";

    public enum GrowthKey {
        ATTACK_DAMAGE("attack_damage", "Attack Damage", 0.004f, "+", false),
        ATTACK_SPEED("attack_speed", "Attack Speed", 0.0004f, "+", false),
        DRAW_SPEED("draw_speed", "Draw Speed", 0.0006f, "+", true),
        MINING_SPEED("mining_speed", "Effective Mining", 0.0004f, "+", true),
        ALT_MINING_SPEED("alt_mining_speed", "Ineffective Mining", 0.0006f, "+", true),
        ENTITY_DAMAGE_REDUCTION("entity_damage_reduction", "Entity Damage", 0.000086f, "-", true),
        ENVIRONMENTAL_DAMAGE_REDUCTION("environmental_damage_reduction", "Environmental Damage", 0.00016f, "-", true);

        public final String id;
        public final String name;
        public final float scale;
        public final String prefix;
        public final boolean isPercentage;

        GrowthKey(String id, String name, float scale, String prefix, boolean isPercentage) {
            this.id = id;
            this.name = name;
            this.scale = scale;
            this.prefix = prefix;
            this.isPercentage = isPercentage;
        }
    }

    public static void addGrowthPoints(ItemStack itemStack, GrowthKey key, float value) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtCompound growthPoints = nbt.getCompound(GROWTH_NBT_KEY);
        float sum = 0;
        for (String nbtKey : growthPoints.getKeys()) {
            sum += growthPoints.getFloat(nbtKey);
        }
        if (sum < 1000) {
            growthPoints.putFloat(key.id, Math.min(growthPoints.getFloat(key.id) + value, 1000));
        }
        nbt.put(GROWTH_NBT_KEY, growthPoints);
    }

    public static float getGrowthPoints(ItemStack itemStack, GrowthKey key) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.contains(GROWTH_NBT_KEY)) {
            return nbt.getCompound(GROWTH_NBT_KEY).getFloat(key.id);
        }
        return 0;
    }

    public static float getGrowthStat(ItemStack itemStack, GrowthKey key) {
        return getGrowthPoints(itemStack, key) * key.scale;
    }

    public static float getTotalGrowthPoints(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        float total = 0.0f;
        if (nbt != null && nbt.contains(GROWTH_NBT_KEY)) {
            for (GrowthKey key : GrowthKey.values()) {
                total += nbt.getCompound(GROWTH_NBT_KEY).getFloat(key.id);
            }
        }
        return total;
    }

    public static Map<GrowthKey, Float> getAllGrowthPoints(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        Map<GrowthKey, Float> map = new LinkedHashMap<>();
        if (nbt != null && nbt.contains(GROWTH_NBT_KEY)) {
            for (GrowthKey key : GrowthKey.values()) {
                float f = nbt.getCompound(GROWTH_NBT_KEY).getFloat(key.id);
                if (f > 0) {
                    map.put(key, f);
                }
            }
        }
        return map;
    }
}
