package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.items.BoomerangItem;
import com.birblett.lib.creational.ContractBuilder;
import com.birblett.lib.creational.CurseBuilder;
import com.birblett.lib.creational.EnchantmentBuilder;
import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Enchantment instantiation and registration.
 */
public class SupplementaryEnchantments {

    /**
     * Valid equipment slots of enchantments to be checked against by
     * {@link net.minecraft.enchantment.EnchantmentHelper#getEquipmentLevel(Enchantment, LivingEntity)}
     */
    public static final EquipmentSlot[] ALL_SLOTS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
            EquipmentSlot.FEET};
    public static final EquipmentSlot[] NONE = new EquipmentSlot[]{};

    /**
     * <hr><center><h1>General enchantments</h1></center><hr>
     * These are applicable to most enchantable items. <br><br>
     * Empowered - All other enchantments' effective level is increased by 1. Max lvl: 1 <br>
     * Enhanced - Slightly modifies the effects of other enchantments. Max lvl: 1 <br>
     * Growth - Continually gains small amounts of base stats while being used. Max lvl: 1 <br>
     * Soulbound - Stays in inventory on death. Max lvl: 1
     */
    public static final EnchantmentBuilder EMPOWERED = new EnchantmentBuilder("empowered", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, NONE);
    public static final EnchantmentBuilder ENHANCED = new EnchantmentBuilder("enhanced", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, ALL_ARMOR);
    public static final EnchantmentBuilder GROWTH = new EnchantmentBuilder("growth", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, NONE){
        @Override
        public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item) {
            // Gives 1 draw speed growth on arrow fire, and an extra if fully charged
            if (projectileEntity instanceof ArrowEntity arrow) {
                int growthAmount = arrow.isCritical() && item.getItem() instanceof BowItem ? 2 : 1;
                SupplementaryEnchantmentHelper.addGrowthPoints(item, SupplementaryEnchantmentHelper.GrowthKey.DRAW_SPEED, growthAmount);
            }
            // Gives 2 flat draw speed growth on trident throw
            else if (projectileEntity instanceof TridentEntity trident) {
                SupplementaryEnchantmentHelper.addGrowthPoints(item, SupplementaryEnchantmentHelper.GrowthKey.DRAW_SPEED, 2);
                trident.tridentStack = item;
            }
        }

        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            // Gives 1 point of growth to either attack damage or speed if the damage is critical or not respectively,
            // or 2 points if the attack is fully charged, and modifies final attack damage with the boosted amount
            if (!user.getWorld().isClient()) {
                int growthAmount = isMaxCharge ? 2 : 1;
                SupplementaryEnchantmentHelper.addGrowthPoints(user.getMainHandStack(), isCritical ? SupplementaryEnchantmentHelper.GrowthKey.ATTACK_DAMAGE :
                        SupplementaryEnchantmentHelper.GrowthKey.ATTACK_SPEED, growthAmount);
            }
            return SupplementaryEnchantmentHelper.getGrowthStat(user.getMainHandStack(), SupplementaryEnchantmentHelper.GrowthKey.ATTACK_DAMAGE);
        }

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item) {
            // Add base mining speed growth stat if appropriate blockstate for the tool, or adaptable if not
            if (item.getMiningSpeedMultiplier(state) > 1) {
                SupplementaryEnchantmentHelper.addGrowthPoints(item, SupplementaryEnchantmentHelper.GrowthKey.MINING_SPEED, 1);
            }
            else {
                SupplementaryEnchantmentHelper.addGrowthPoints(item, SupplementaryEnchantmentHelper.GrowthKey.ALT_MINING_SPEED, 1);
            }
        }

        @Override
        public void onDamage(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            // Add some growth scaling with incoming damage
            if (itemStack.getItem() instanceof ArmorItem) {
                if (source.getAttacker() != null) {
                    SupplementaryEnchantmentHelper.addGrowthPoints(itemStack, SupplementaryEnchantmentHelper.GrowthKey.ENTITY_DAMAGE_REDUCTION, damageAmount.getValue() / 5);
                }
                else if (!(Objects.equals(source.getTypeRegistryEntry().getKey().orElse(DamageTypes.OUT_OF_WORLD), DamageTypes.OUT_OF_WORLD))) {
                    SupplementaryEnchantmentHelper.addGrowthPoints(itemStack, SupplementaryEnchantmentHelper.GrowthKey.ENVIRONMENTAL_DAMAGE_REDUCTION, damageAmount.getValue() / 4);
                }
            }
        }

        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            // Apply damage reduction from Growth
            if (itemStack.getItem() instanceof ArmorItem) {
                if (source.getAttacker() != null) {
                    return 1 - SupplementaryEnchantmentHelper.getGrowthStat(itemStack, SupplementaryEnchantmentHelper.GrowthKey.ENTITY_DAMAGE_REDUCTION);
                }
                else if (!(Objects.equals(source.getTypeRegistryEntry().getKey().orElse(DamageTypes.OUT_OF_WORLD), DamageTypes.OUT_OF_WORLD))) {
                    return 1 - SupplementaryEnchantmentHelper.getGrowthStat(itemStack, SupplementaryEnchantmentHelper.GrowthKey.ENVIRONMENTAL_DAMAGE_REDUCTION);
                }
            }
            return 1.0f;
        }
    };
    public static final EnchantmentBuilder SOULBOUND = new EnchantmentBuilder("soulbound", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, NONE);

    /**
     * <hr><center><h1>Boomerang enchantments</h1></center><hr>
     * Pickup - boomerangs can pick up items, with larger inventories based on level. Max lvl: 3
     */
    public static final EnchantmentBuilder PICKUP = new EnchantmentBuilder("pickup", Enchantment.Rarity.UNCOMMON, null, BOTH_HANDS);

    /**
     * <hr><center><h1>Bow enchantments</h1></center><hr>
     * Hitscan - Arrows instantly travel to their destination. Max lvl: 1 <br>
     * Lightning Bolt - Summon lightning on projectile hit, provided it has sky access. Max lvl: 1 <br>
     * Oversized - Longer draw time. Projectiles are bigger and faster, with higher damage. Max lvl: 2 <br>
     * Marked - On entity hit: set a marked entity. Subsequent arrows will home in on this entity. Max lvl: 3
     */
    public static final EnchantmentBuilder HITSCAN = new EnchantmentBuilder("hitscan", Enchantment.Rarity.RARE, EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.RARE, EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder OVERSIZED = new EnchantmentBuilder("oversized", Enchantment.Rarity.UNCOMMON, EnchantmentTarget.BOW, BOTH_HANDS);

    /**
     * <hr><center><h1>Crossbow enchantments</h1></center><hr>
     * Burst fire - Arrows are fired in bursts of 3. Damage and velocity are slightly decreased. Max lvl: 1
     */
    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.RARE, EnchantmentTarget.CROSSBOW, BOTH_HANDS);

    /**
     * <hr><center><h1>Sword enchantments</h1></center><hr>
     * Frantic - Critical hits grant a short speed boost. Deal extra damage while speedy. Max lvl: 3 <br>
     * Frenzy - Take more damage, deal more at lower health. Max lvl: 3
     */
    public static final EnchantmentBuilder FRANTIC = new EnchantmentBuilder("frantic", Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            float modifier = 0.0f;
            if (!user.getWorld().isClient()) {
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
    public static final EnchantmentBuilder FRENZY = new EnchantmentBuilder("frenzy", Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            float lostHealthPercent = 2 * Math.max(0.5f, 1 - user.getHealth() / user.getMaxHealth());
            return lostHealthPercent * 0.2f * level * damageAmount;
        }

        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            return source.getAttacker() != null ? 1.2f : 1.0f;
        }
    };

    /**
     * <hr><center><h1>Mobility enchantments</h1></center><hr>
     * These are various enchantments that all increase user mobility in some way. <br><br>
     * Acrobatic - Boots: wall cling/jump while sneaking and double jump. Max lvl: 1 <br>
     * Air Dash - Boots: dash in the air on double-tapping forward. Max lvl: 1 <br>
     * All Terrain - Boots: increased step height, and ability to walk on water. Max lvl: 1 <br>
     * Assault Dash - Shields: charge forward while holding shield up, knocking entities away. Max lvl: 2 <br>
     * Bunnyhop - Boots: decrease height, increase speed on jump. Scale small vertical gaps in the air. Max Lvl: 1 <br>
     * Grappling - Bows, crossbows, fishing rods: projectiles pull the user in. Varies by tool type. Max lvl: 1 <br>
     * Slimed - Boots: become bouncy, experience much less friction. Max lvl: 1 <br>
     * Strafe - Boots: double tap the left/right movement keys to strafe left or right while grounded, swimming, or flying. Max lvl: 1
     */
    public static final EnchantmentBuilder ACROBATIC = new EnchantmentBuilder("acrobatic", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder AIR_DASH = new EnchantmentBuilder("air_dash", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder ALL_TERRAIN = new EnchantmentBuilder("all_terrain", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder ASSAULT_DASH = new EnchantmentBuilder("assault_dash", Enchantment.Rarity.VERY_RARE, null, BOTH_HANDS);
    public static final EnchantmentBuilder BUNNYHOP = new EnchantmentBuilder("bunnyhop", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder GRAPPLING = new EnchantmentBuilder("grappling", Enchantment.Rarity.UNCOMMON, EnchantmentTarget.CROSSBOW, MAIN_HAND);
    public static final EnchantmentBuilder SLIMED = new EnchantmentBuilder("slimed", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    public static final EnchantmentBuilder STRAFE = new EnchantmentBuilder("strafe", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);

    /**
     * <hr><center><h1>Curses</h1></center><hr>
     * Various curses with negative drawbacks. If enough curses are present, contracts can be applied. Vanilla curses can
     * also contribute to this. <br><br>
     * Atrophy - Melee attacks and bow drawing charge 10% slower per level. Max level: 3. Curse points: 1 per lvl <br>
     * Backlash - Melee attacks incur a backlash of 1 damage per level, ignoring armor, enchantments, and resistance. Max
     * level: 2. Curse points: 3 per lvl <br>
     * Blighted - Disables natural health regeneration. At level 2, effect lasts until death. Max lvl: 2. Curse points: 3 (lvl 1),
     * 8 (lvl 2) <br>
     * Fragility - Take (10 + 5 * level)% more damage from entities and projectiles. Max level: 5. Curse points: 1 per lvl <br>
     * Gluttony - Exhaustion/hunger accumulates 30% faster per level. Saturation is decreased by 30%. Max lvl: 2. Curse
     * points: 2 * lvl - 1 <br>
     */
    public static final CurseBuilder ATROPHY = new CurseBuilder("atrophy", Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl);
    public static final CurseBuilder BACKLASH = new CurseBuilder("backlash", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl * 2) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            user.damage(SupplementaryEnchantmentHelper.backlash(user.getWorld()), Math.min(level, 2));
            return 0.0f;
        }
    };
    public static final CurseBuilder BLIGHTED = new CurseBuilder("blighted", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl > 1 ? 8 : 3);
    public static final CurseBuilder FRAGILITY = new CurseBuilder("fragility", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, lvl -> lvl) {
        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            return 1.0f + 0.1f * level;
        }
    };
    public static final CurseBuilder GLUTTONY = new CurseBuilder("gluttony", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl * 2 - 1);

    /**
     * <hr><center><h1>Contracts</h1></center><hr>
     * Powerful effects that can only be applied if an item has enough curses on it. None are obtainable via random
     * enchantment - they must be applied via anvil. <br><br>
     *
     * Cursed Power - Increases all melee damage dealt by 10% per curse point, with a penalty of 20% per contract present.
     * Curse points required: 7 <br>
     * Magic Guard - Negates many forms of indirect (not from living entities or self) damage entirely. Requires any level
     * of Fragility. Curse points required: 6 <br>
     * Vigor - Increases health by 10 points/5 hearts. Prevents fatal damage once per life, but health bonus is removed
     * until next death. Curse points required: 7 <br>
     */
    public static final ContractBuilder CURSED_POWER = new ContractBuilder("cursed_power", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, ContractBuilder.NO_OP, 7);
    public static final ContractBuilder MAGIC_GUARD = new ContractBuilder("magic_guard", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, stack -> EnchantmentHelper.getLevel(FRAGILITY, stack) > 0, 5);
    public static final ContractBuilder VIGOR = new ContractBuilder("vigor", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, ContractBuilder.NO_OP, 7);

    /**
     * Sets of enchantments incompatible with each other.
     */
    public static final EnchantmentBuilder[] GENERAL_COMPATIBILITY_GROUP = {EMPOWERED, ENHANCED, GROWTH, SOULBOUND};
    public static final EnchantmentBuilder[] MOBILITY_INCOMPATIBILITY_GROUP = {ACROBATIC, AIR_DASH, ALL_TERRAIN, BUNNYHOP, SLIMED};

    /**
     * Sets enchantment attributes and registers enchantments. Called from {@link Supplementary#onInitialize()}. See
     * {@link EnchantmentBuilder} for specific builder methods.
     */
    public static void register() {
        // region general
        EMPOWERED.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
        ENHANCED.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.ENHANCED)
                .setTreasure(true)
                .build();
        GROWTH.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
        SOULBOUND.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setTreasure(true)
                .build();
        // endregion
        // region boomerang
        PICKUP.setPower(10, 10, 20, 20)
                .setMaxLevel(3)
                .addCompatibleClasses(BoomerangItem.class)
                .build();
        // endregion
        // region bow
        HITSCAN.makeIncompatible(OVERSIZED, MARKED)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.HITSCAN)
                .build();
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER, OVERSIZED)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.LIGHTNING_BOLT)
                .build();
        MARKED.setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponent(SupplementaryComponents.MARKED)
                .build();
        OVERSIZED.makeIncompatible(LIGHTNING_BOLT)
                .setPower(20, 5, 30, 10)
                .setMaxLevel(2)
                .addComponent(SupplementaryComponents.OVERSIZED)
                .build();
        // endregion
        // region crossbow
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50)
                .addComponent(SupplementaryComponents.BURST_FIRE)
                .build();
        // endregion
        // region sword
        FRANTIC.makeIncompatible(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK)
                .setPower(15, 5, 25, 5)
                .setMaxLevel(3)
                .build();
        FRENZY.makeIncompatible(FRANTIC)
                .setPower(20, 10, 30, 10)
                .setMaxLevel(3)
                .build();
        // endregion
        // region mobility
        ACROBATIC.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setCustomAnvilCost(4)
                .build();
        AIR_DASH.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setCustomAnvilCost(4)
                .build();
        ALL_TERRAIN.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .setCustomAnvilCost(2)
                .build();
        ASSAULT_DASH.setPower(15, 15, 30, 15)
                .setMaxLevel(2)
                .setCustomAnvilCost(8)
                .addCompatibleClasses(ShieldItem.class)
                .build();
        BUNNYHOP.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .addAttribute("bunnyhop_move_speed", EntityAttributes.GENERIC_MOVEMENT_SPEED, Operation.MULTIPLY_TOTAL,
                        (entity, stack, lvl) -> -0.15)
                .build();
        GRAPPLING.makeIncompatible(Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT)
                .setPower(20,50)
                .addComponent(SupplementaryComponents.GRAPPLING)
                .setTreasure(true)
                .addCompatibleClasses(FishingRodItem.class, BowItem.class)
                .addCompatibleItems(Items.CROSSBOW)
                .build();
        SLIMED.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        STRAFE.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                .setPower(20, 50)
                .build();
        // endregion

        // region curses
        ATROPHY.setPower(20, 5, 50, 5)
                .setMaxLevel(3)
                .setCustomAnvilCost(1)
                .addAttribute("atrophy_attack_speed", EntityAttributes.GENERIC_ATTACK_SPEED, Operation.MULTIPLY_TOTAL,
                        (entity, stack, lvl) -> lvl * -0.1)
                .build();
        BACKLASH.setPower(20, 10, 50, 10)
                .setMaxLevel(2)
                .setCustomAnvilCost(2)
                .build();
        BLIGHTED.setPower(20, 10, 50, 10)
                .setMaxLevel(2)
                .setCustomAnvilCost(2)
                .build();
        FRAGILITY.setPower(20, 2, 50, 2)
                .setMaxLevel(5)
                .setCustomAnvilCost(1)
                .build();
        GLUTTONY.setPower(20, 10, 50, 10)
                .setMaxLevel(2)
                .setCustomAnvilCost(2)
                .build();
        // endregion
        // region contracts
        CURSED_POWER.setPower(50, 50)
                .setCustomAnvilCost(0)
                .addAttribute("cursed_power_damage_bonus", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL,
                        (entity, stack, lvl) -> (SupplementaryEnchantmentHelper.getCursePoints(stack, -2) - 2) * 0.1)
                .build();
        MAGIC_GUARD.setPower(50, 50)
                .setCustomAnvilCost(0)
                .build();
        VIGOR.setPower(50, 50)
                .setCustomAnvilCost(0)
                .addAttribute("vigor_health_bonus", EntityAttributes.GENERIC_MAX_HEALTH, Operation.ADDITION,
                        (entity, stack, lvl) -> {
                            AtomicBoolean vigorRevive = new AtomicBoolean(false);
                            SupplementaryComponents.VIGOR.maybeGet(entity).ifPresent(component -> vigorRevive.set((Boolean) component.getValue()));
                            return vigorRevive.get() ? 0 : 10.0;
                        })
                .build();
        // endregion
    }
}
