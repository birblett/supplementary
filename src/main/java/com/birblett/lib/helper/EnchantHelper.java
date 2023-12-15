package com.birblett.lib.helper;

import com.birblett.Supplementary;
import com.birblett.lib.creational.ContractBuilder;
import com.birblett.lib.creational.CurseBuilder;
import com.birblett.registry.SupplementaryAttributes;
import com.birblett.registry.SupplementaryEnchantments;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Used to get certain attributes/values associated with enchantments. Will be used for serverconfig in the future.
 */
public class EnchantHelper {

    /**
     * <hr><center><h1>General helpers</h1></center><hr>
     * Utilities for functionalities that may not require a full event hook or scalable mixin to process.<br><br>
     *
     * Essentially a rewrite of vanilla {@link net.minecraft.item.BowItem#getPullProgress(int)} to reduce reliance on shared
     * variables between static and non-static contexts.
     * @param holder User of the bow
     * @param useTicks Number of ticks bow has been drawn
     * @param stack Associated ItemStack for bow
     * @return Scaled pull progress value
     */
    public static float customPullProgress(LivingEntity holder, int useTicks, ItemStack stack) {
        float f = useTicks * (float) holder.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 200.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    /**
     * Essentially a rewrite of vanilla {@link net.minecraft.item.CrossbowItem#getPullProgress(int, ItemStack)} to reduce
     * reliance on shared variables between static and non-static contexts. Replaces instances where it's called.
     * @param holder User of the bow
     * @param useTicks Number of ticks bow has been drawn
     * @param stack Associated ItemStack for crossbow
     * @return Scaled pull progress value
     */
    public static float customCrossbowPullProgress(int useTicks, LivingEntity holder, ItemStack stack) {
        float f = (float)useTicks / (float) CrossbowItem.getPullTime(stack);
        if (holder.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            f *= (float) holder.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 10.0f;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    /**
     * Rewrite of vanilla {@link net.minecraft.item.CrossbowItem#getPullTime(ItemStack)} to reduce reliance on shared variables
     * between static and non-static contexts. Replaces instances where it's called. Math.ceil() necessary so Pillager attack
     * goal does not break.
     * @param holder User of the bow
     * @param stack Associated ItemStack for crossbow
     * @return Scaled pull time
     */
    public static int customCrossbowPullTime(@Nullable LivingEntity holder, ItemStack stack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        double j = holder != null && holder.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null ? holder.getAttributeValue(
                SupplementaryAttributes.DRAW_SPEED) / 10.0f : 1;
        return (int) Math.ceil(Math.max((25 / j) - 5 * i, 0));
    }

    /**
     * Get total number of curse points on the provided item.
     * @param stack ItemStack to evaluate
     * @param customContractPenalty Custom value to decrement per contract, instead of using contract curse point requirement
     * @return Total number of curse points on the item
     */
    public static int getCursePoints(ItemStack stack, int customContractPenalty) {
        AtomicDouble cursePoints = new AtomicDouble(0);
        EnchantmentHelper.fromNbt(stack.getEnchantments()).forEach((ench, lvl) -> {
            // contracts present reduce the total number of curse points,
            if (ench instanceof ContractBuilder contract) {
                cursePoints.addAndGet(customContractPenalty == 0 ? -contract.cursePointRequirement : customContractPenalty);
            }
            // if custom curse, add curse points based on enchantment level
            if (ench instanceof CurseBuilder curse) {
                cursePoints.addAndGet(curse.getCursePoints(lvl));
            }
            // default vanilla curses provide 2 curse points
            else if (ench.equals(Enchantments.BINDING_CURSE) || ench.equals(Enchantments.VANISHING_CURSE)) {
                cursePoints.addAndGet(2);
            }
            // modded curses provide 1 curse point
            else if (ench.isCursed()) {
                cursePoints.addAndGet(1);
            }
        });
        return (int) cursePoints.get();
    }

    /**
     * <hr><center><h1>Damage Types</h1></center><hr>
     * Damage sources for different enchantments<br><br>
     *
     * Damage source for the Assault Dash enchantment
     * @param source the source entity
     * @return a new EntityDamageSource, with custom death message
     */
    public static DamageSource assaultDash(LivingEntity source) {
        return new DamageSource(source.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RegistryKey
                .of(RegistryKeys.DAMAGE_TYPE,
                new Identifier("supplementary", "assault_dash")))) {
            @Override
            public Text getDeathMessage(LivingEntity entity) {
                if (this.getAttacker() != null)
                    return MutableText.of(new TranslatableTextContent("death.attack." + this.getName() + ".player",
                            null, new Object[]{entity.getDisplayName().getString(), this.getAttacker().getDisplayName()
                            .getString()}));
                else
                    return MutableText.of(new TranslatableTextContent("death.attack." + this.getName() + ".fallback",
                            null, new Object[]{entity.getDisplayName().getString()}));
            }
        };
    }

    /**
     * Damage source for the Backlash curse
     * @param world the world in which the damage was taken
     * @return a new EntityDamageSource, with custom death message
     */
    public static DamageSource backlash(World world) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                new Identifier("supplementary", "backlash")))) {
            @Override
            public Text getDeathMessage(LivingEntity entity) {
                return MutableText.of(new TranslatableTextContent("death.attack." + this.getName() + ".player",
                        null, new Object[]{entity.getDisplayName().getString()}));
            }
        };
    }
    public static final RegistryKey<DamageType> BACKLASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Supplementary.MODID,
            "backlash"));

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


    /**
     * <hr><center><h1>Magic Guard enchantment helpers</h1></center><hr>
     * Damage type tag for indirect damage
     */
    public static final TagKey<DamageType> INDIRECT_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Supplementary.MODID,
            "indirect"));
}
