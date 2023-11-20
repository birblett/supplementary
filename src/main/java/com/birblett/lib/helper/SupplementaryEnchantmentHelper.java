package com.birblett.lib.helper;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Used to get certain attributes/values associated with enchantments. Will be used for serverconfig in the future.
 */
public class SupplementaryEnchantmentHelper {

    /**
     * <hr><center><h1>General helpers</h1></center><hr><br><br>
     *
     * Apply draw speed modifiers for enchantments
     * @param base Base amount
     * @param stack Bow/crossbow itemstack
     * @return Draw speed after applying modifiers
     */
    public static float getDrawspeedModifier(float base, ItemStack stack) {
        stack = stack == null ? ItemStack.EMPTY : stack;
        float oversizedModifier = (float) Math.pow(0.75, net.minecraft.enchantment.EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack));
        float growthModifier = 1 + getGrowthStat(stack, GrowthKey.DRAW_SPEED);
        return base * oversizedModifier * growthModifier;
    }

    /**
     * <hr><center><h1>Assault Dash enchantment helpers</h1></center><hr>
     * Helper functions for operations involving the Enhanced enchantment<br><br>
     *
     * Damage source for the Assault Dash enchantment
     * @param source the source entity
     * @return a new EntityDamageSource, with custom death message
     */
    public static DamageSource assaultDash(LivingEntity source) {
        return new DamageSource(source.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("supplementary", "assault_dash")))) {
            @Override
            public Text getDeathMessage(LivingEntity entity) {
                if (this.getAttacker() != null)
                    return MutableText.of(new TranslatableTextContent("death.attack." + this.getName() + ".player", null,
                            new Object[]{entity.getDisplayName().getString(), this.getAttacker().getDisplayName().getString()}));
                else
                    return MutableText.of(new TranslatableTextContent("death.attack." + this.getName() + ".fallback",
                            null, new Object[]{entity.getDisplayName().getString()}));
            }
        };
    }

    /**
     * <hr><center><h1>Growth enchantment helpers</h1></center><hr>
     * Enum and helper functions for operations involving the Growth enchantment
     */
    public static final String GROWTH_NBT_KEY = "SupplementaryGrowthStats";

    /**
     * Keys, names, stat scaling, and display values for Growth attributes.
     */
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

    /**
     * Adds growth points of a specific stat to an item
     * @param itemStack Provided item
     * @param key Growth stat to increment
     * @param value Amount of growth points to add
     */
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

    /**
     * Get the amount of growth points of a certain stat on an item
     * @param itemStack Provided item
     * @param key Stat to be checked
     * @return Amount of growth points; 0 if item does not have the growth stat
     */
    public static float getGrowthPoints(ItemStack itemStack, GrowthKey key) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.contains(GROWTH_NBT_KEY)) {
            return nbt.getCompound(GROWTH_NBT_KEY).getFloat(key.id);
        }
        return 0;
    }

    /**
     * Get the modifier associated with the provided growth key on the item
     * @param itemStack Provided item
     * @param key Stat to be checked
     * @return Growth modifier
     */
    public static float getGrowthStat(ItemStack itemStack, GrowthKey key) {
        return getGrowthPoints(itemStack, key) * key.scale;
    }

    /**
     * Get the total number of growth points on an item
     * @param itemStack Provided item
     * @return Amount of growth points
     */
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

    /**
     * Return a map of all growth stat points on an item
     * @param itemStack Provided item
     * @return A map of all growth points on the provided item, following the GrowthKey enum ordering
     */
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


    /**
     * <hr><center><h1>Enhanced enchantment helpers</h1></center><hr>
     * Helper functions for operations involving the Enhanced enchantment<br><br>
     *
     * Return a knockback reduction multiplier based on protection levels and the incoming damage type
     * @param entity The entity to be checked against
     * @param source Provided damage source
     * @return Knockback reduction multiplier
     */
    public static float enhancedProtKnockbackAmount(LivingEntity entity, DamageSource source) {
        float mult = 1.0f;
        for (ItemStack stack : entity.getArmorItems()) {
            if (EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, stack) > 0) {
                for (Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.get(stack).entrySet()) {
                    mult -= 0.025f * enchantment.getKey().getProtectionAmount(enchantment.getValue(), source);
                }
            }
        }
        return Math.max(mult, 0.0f);
    }

    public static int getEnhancedEquipLevel(Enchantment enchantment, LivingEntity entity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(entity).values();
        int i = 0;
        for (ItemStack itemStack : iterable) {
            int j = EnchantmentHelper.getLevel(enchantment, itemStack);
            if (j <= i || EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, itemStack) == 0) continue;
            i = j;
        }
        return i;
    }
}
