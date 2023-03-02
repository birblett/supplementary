package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.items.BoomerangItem;
import com.birblett.lib.creational.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * Enchantment instantiation and registration.
 */
public class SupplementaryEnchantments {

    /**
     * Valid equipment slots of enchantments to be checked against by
     * {@link net.minecraft.enchantment.EnchantmentHelper#getEquipmentLevel(Enchantment, LivingEntity)}
     */
    public static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final EquipmentSlot[] NONE = new EquipmentSlot[]{};

    /**
     * Damage source for the Assault Dash enchantment
     * @param source the source entity
     * @return a new EntityDamageSource, with custom death message
     */
    public static DamageSource shieldBash(LivingEntity source) {
        return new EntityDamageSource("shield_bash", source) {
            @Override
            public Text getDeathMessage(LivingEntity entity) {
                return new TranslatableText("death.attack." + this.name + ".player", entity.getDisplayName(), this.source.getDisplayName());
            }
        };
    }

    /**
     * General enchantments that are applicable to most enchantable items <br>
     * Empowered - All other enchantments' effective level is increased by 1. Max lvl: 1 <br>
     * Soulbound - Stays in inventory on death. Max lvl: 1
     */
    public static final EnchantmentBuilder EMPOWERED = new EnchantmentBuilder("empowered", Enchantment.Rarity.RARE,
            EnchantmentTarget.BREAKABLE, NONE);
    public static final EnchantmentBuilder SOULBOUND = new EnchantmentBuilder("soulbound", Enchantment.Rarity.RARE,
            EnchantmentTarget.BREAKABLE, NONE);

    /**
     * Boomerang enchantments <br>
     * Pickup - boomerangs can pick up items, with larger inventories based on level. Max lvl: 3
     */
    public static final EnchantmentBuilder PICKUP = new EnchantmentBuilder("pickup", Enchantment.Rarity.UNCOMMON,
            null, BOTH_HANDS);

    /**
     * Bow enchantments <br>
     * Lightning Bolt - Summon lightning on projectile hit, provided it has sky access. Max lvl: 1 <br>
     * Oversized - Longer draw time. Projectiles are bigger and faster, with higher damage. Max lvl: 2 <br>
     * Marked - On entity hit: set a marked entity. Subsequent arrows will home in on this entity. Max lvl: 3
     */
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder OVERSIZED = new EnchantmentBuilder("oversized", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);

    /**
     * Crossbow enchantments <br>
     * Burst fire - Arrows are fired in bursts of 3. Damage and velocity are slightly decreased. Max lvl: 1
     */
    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);

    /**
     * Sword enchantments <br>
     * Frantic - Critical hits grant a short speed boost. Deal extra damage while speedy. Max lvl: 3 <br>
     * Frenzy - Take more damage, deal more at lower health. Max lvl: 3
     */
    public static final EnchantmentBuilder FRANTIC = new EnchantmentBuilder("frantic", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, float damageAmount) {
            float modifier = 0.0f;
            if (!user.world.isClient()) {
                if (user.hasStatusEffect(StatusEffects.SPEED)) {
                    modifier = 0.1f * level * damageAmount;
                }
                if (isCritical) {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 50));
                }
            }
            return modifier;
        }
    };
    public static final EnchantmentBuilder FRENZY = new EnchantmentBuilder("frenzy", Enchantment.Rarity.RARE,
            EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, float damageAmount) {
            float lostHealthPercent = 2 * Math.max(0.5f, 1 - user.getHealth() / user.getMaxHealth());
            return lostHealthPercent * 0.2f * level * damageAmount;
        }

        @Override
        public float onDamage(LivingEntity user, DamageSource source, int level, float damageAmount) {
            return source.getAttacker() != null ? damageAmount * 0.2f : 0.0f;
        }
    };

    /**
     * Mobility enchantments <br>
     * Acrobatic - Boots: wall cling/jump while sneaking and double jump. Max lvl: 1 <br>
     * Air Dash - Boots: dash in the air on double-tapping forward. Max lvl: 1 <br>
     * All Terrain - Boots: increased step height, and ability to walk on fluids. Max lvl: 1 <br>
     * Assault Dash - Shields: charge forward while holding shield up, knocking entities away. Max lvl: 2 <br>
     * Bunnyhop - Boots: decrease height, increase speed on jump. Scale small vertical gaps in the air. Max Lvl: 1 <br>
     * Grappling - Bows, crossbows, fishing rods: projectiles pull the user in. Varies by tool type. Max lvl: 1 <br>
     * Slimed - Boots: become bouncy, experience much less friction. Max lvl: 1
     */
    public static final EnchantmentBuilder ACROBATIC = new EnchantmentBuilder("acrobatic", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder AIR_DASH = new EnchantmentBuilder("air_dash", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder ALL_TERRAIN = new EnchantmentBuilder("all_terrain", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder ASSAULT_DASH = new EnchantmentBuilder("assault_dash", Enchantment.Rarity.VERY_RARE,
            null, BOTH_HANDS);
    public static final EnchantmentBuilder BUNNYHOP = new EnchantmentBuilder("bunnyhop", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder GRAPPLING = new EnchantmentBuilder("grappling", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.CROSSBOW, MAIN_HAND);
    public static final EnchantmentBuilder SLIMED = new EnchantmentBuilder("slimed", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);

    /**
     * Sets of enchantments incompatible with each other.
     */
    public static final EnchantmentBuilder[] GENERAL_COMPATIBILITY_GROUP = {EMPOWERED, SOULBOUND};
    public static final EnchantmentBuilder[] MOBILITY_INCOMPATIBILITY_GROUP = {ACROBATIC, AIR_DASH, ALL_TERRAIN, BUNNYHOP, SLIMED};

    /**
     * Sets enchantment attributes and registers enchantments. Called from {@link Supplementary#onInitialize()}. See
     * {@link EnchantmentBuilder} for specific builder methods.
     */
    public static void register() {
        ACROBATIC.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        AIR_DASH.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        ALL_TERRAIN.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        ASSAULT_DASH.setPower(15, 15, 30, 15)
                .setMaxLevel(2)
                .addCompatibleClasses(ShieldItem.class)
                .build();
        BUNNYHOP.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.BURST_FIRE_TIMER)
                .build();
        EMPOWERED.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
        FRANTIC.makeIncompatible(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK)
                .setPower(15, 5, 25, 5)
                .setMaxLevel(3)
                .build();
        FRENZY.makeIncompatible(FRANTIC)
                .setPower(20, 10, 30, 10)
                .setMaxLevel(3)
                .build();
        GRAPPLING.makeIncompatible(Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT)
                .setPower(20,50)
                .addComponent(SupplementaryComponents.GRAPPLING)
                .setTreasure(true)
                .addCompatibleClasses(FishingRodItem.class, BowItem.class)
                .addCompatibleItems(Items.CROSSBOW)
                .build();
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER, OVERSIZED)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.LIGHTNING_BOLT)
                .build();
        MARKED.setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponent(SupplementaryComponents.MARKED_LEVEL)
                .build();
        OVERSIZED.makeIncompatible(LIGHTNING_BOLT)
                .setPower(20, 5, 30, 10)
                .setMaxLevel(2)
                .addComponent(SupplementaryComponents.OVERSIZED_PROJECTILE)
                .build();
        PICKUP.setPower(10, 10, 20, 20)
                .setMaxLevel(3)
                .addCompatibleClasses(BoomerangItem.class)
                .build();
        SLIMED.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        SOULBOUND.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
    }
}
