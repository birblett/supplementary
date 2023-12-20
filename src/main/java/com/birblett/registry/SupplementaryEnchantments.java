package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.items.BoomerangItem;
import com.birblett.lib.api.StaticFadingBlock;
import com.birblett.lib.creational.ContractBuilder;
import com.birblett.lib.creational.CurseBuilder;
import com.birblett.lib.creational.EnchantmentBuilder;
import com.birblett.lib.helper.EnchantHelper;
import com.birblett.lib.helper.GenMathHelper;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
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
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.awt.*;
import java.util.List;
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
     * These are applicable to most enchantable items.
     */
    public static final List<EnchantmentBuilder> GENERAL;
    /**
     * Empowered - All other enchantments' effective level is increased by 1. Max lvl: 1
     */
    public static final EnchantmentBuilder EMPOWERED = new EnchantmentBuilder("empowered", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, NONE);
    /**
     * Enhanced - Slightly modifies the effects of other enchantments. Max lvl: 1
     */
    public static final EnchantmentBuilder ENHANCED = new EnchantmentBuilder("enhanced", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, ALL_ARMOR);
    /**
     * Growth - Continually gains small amounts of base stats while being used. Max lvl: 1
     */
    public static final EnchantmentBuilder GROWTH = new EnchantmentBuilder("growth", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, ALL_SLOTS){
        @Override
        public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item) {
            // Gives 1 draw speed growth on arrow fire, and an extra if fully charged
            if (projectileEntity instanceof ArrowEntity arrow) {
                int growthAmount = arrow.isCritical() && item.getItem() instanceof BowItem ? 2 : 1;
                EnchantHelper.addGrowthPoints(item, EnchantHelper.GrowthKey.DRAW_SPEED, growthAmount);
            }
            // Gives 2 flat draw speed growth on trident throw
            else if (projectileEntity instanceof TridentEntity trident) {
                EnchantHelper.addGrowthPoints(item, EnchantHelper.GrowthKey.DRAW_SPEED, 2);
                trident.tridentStack = item;
            }
        }

        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            // Gives 1 point of growth to either attack damage or speed if the damage is critical or not respectively,
            // or 2 points if the attack is fully charged, and modifies final attack damage with the boosted amount
            if (!user.getWorld().isClient()) {
                int growthAmount = isMaxCharge ? 2 : 1;
                EnchantHelper.addGrowthPoints(user.getMainHandStack(), isCritical ? EnchantHelper.GrowthKey.ATTACK_DAMAGE :
                        EnchantHelper.GrowthKey.ATTACK_SPEED, growthAmount);
            }
            return 0;
        }

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            if (!isClient && !miner.isCreative()) {
                // Add base mining speed growth stat if appropriate blockstate for the tool, or adaptable if not
                if (item.getMiningSpeedMultiplier(state) > 1) {
                    EnchantHelper.addGrowthPoints(item, EnchantHelper.GrowthKey.MINING_SPEED, 1);
                } else {
                    EnchantHelper.addGrowthPoints(item, EnchantHelper.GrowthKey.ALT_MINING_SPEED, 1);
                }
            }
        }

        @Override
        public void onDamage(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            // Add some growth scaling with incoming damage
            if (itemStack.getItem() instanceof ArmorItem) {
                if (source.getAttacker() != null) {
                    EnchantHelper.addGrowthPoints(itemStack, EnchantHelper.GrowthKey.ENTITY_DAMAGE_REDUCTION, damageAmount.getValue() / 5);
                }
                else if (!(Objects.equals(source.getTypeRegistryEntry().getKey().orElse(DamageTypes.OUT_OF_WORLD), DamageTypes.OUT_OF_WORLD))) {
                    EnchantHelper.addGrowthPoints(itemStack, EnchantHelper.GrowthKey.ENVIRONMENTAL_DAMAGE_REDUCTION, damageAmount.getValue() / 4);
                }
            }
        }

        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            // Apply damage reduction from Growth
            if (itemStack.getItem() instanceof ArmorItem) {
                if (source.getAttacker() != null) {
                    return 1 - EnchantHelper.getGrowthStat(itemStack, EnchantHelper.GrowthKey.ENTITY_DAMAGE_REDUCTION);
                }
                else if (!(Objects.equals(source.getTypeRegistryEntry().getKey().orElse(DamageTypes.OUT_OF_WORLD), DamageTypes.OUT_OF_WORLD))) {
                    return 1 - EnchantHelper.getGrowthStat(itemStack, EnchantHelper.GrowthKey.ENVIRONMENTAL_DAMAGE_REDUCTION);
                }
            }
            return 1.0f;
        }
    };
    /**
     * Soulbound - Stays in inventory on death. Max lvl: 1
     */
    public static final EnchantmentBuilder SOULBOUND = new EnchantmentBuilder("soulbound", Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, NONE);

    public static final EnchantmentBuilder[] GENERAL_COMPATIBILITY_GROUP = {EMPOWERED, ENHANCED, GROWTH, SOULBOUND};
    static {
        GENERAL = List.of(
                EMPOWERED.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setTreasure(true),
                ENHANCED.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .addComponent(SupplementaryComponents.ENHANCED)
                        .setTreasure(true),
                GROWTH.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setTreasure(true)
                        .addAttribute("growth_attack_damage", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.ADDITION,
                                (entity, stack, lvl) -> (double) EnchantHelper.getGrowthStat(entity.getMainHandStack(),
                                        EnchantHelper.GrowthKey.ATTACK_DAMAGE))
                        .addAttribute("growth_attack_speed", EntityAttributes.GENERIC_ATTACK_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> (double) EnchantHelper.getGrowthStat(entity.getMainHandStack(),
                                        EnchantHelper.GrowthKey.ATTACK_SPEED))
                        .addAttribute("growth_draw_speed", SupplementaryAttributes.DRAW_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> (double) EnchantHelper.getGrowthStat(stack, EnchantHelper.GrowthKey.DRAW_SPEED))
                        .addAttribute("growth_effective_mining_speed", SupplementaryAttributes.EFFECTIVE_MINING_SPEED,
                                Operation.MULTIPLY_TOTAL, (entity, stack, lvl) -> (double) EnchantHelper.getGrowthStat(stack,
                                        EnchantHelper.GrowthKey.MINING_SPEED))
                        .addAttribute("growth_ineffective_mining_speed", SupplementaryAttributes.INEFFECTIVE_MINING_SPEED,
                                Operation.MULTIPLY_TOTAL, (entity, stack, lvl) -> (double) EnchantHelper.getGrowthStat(stack,
                                        EnchantHelper.GrowthKey.ALT_MINING_SPEED)),
                SOULBOUND.makeIncompatible(GENERAL_COMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setTreasure(true)
        );
    }

    /**
     * <hr><center><h1>Offensive enchantments</h1></center><hr>
     */
    public static final List<EnchantmentBuilder> OFFENSIVE;
    /**
     * Burst fire (Crossbow) - Arrows are fired in bursts of 3. Damage and velocity are slightly decreased. Max lvl: 1
     */
    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    /**
     * Frantic (Sword) - Critical hits grant a short speed boost. Deal extra damage while speedy. Max lvl: 3
     */
    public static final EnchantmentBuilder FRANTIC = new EnchantmentBuilder("frantic", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.WEAPON, MAIN_HAND) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            if (isCritical) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 50));
            }
            return 0;
        }
    };
    /**
     * Frenzy (Sword) - Take more damage, deal more at lower health. Max lvl: 3
     */
    public static final EnchantmentBuilder FRENZY = new EnchantmentBuilder("frenzy", Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON,
            MAIN_HAND) {

        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            return source.getAttacker() != null ? 1.2f : 1.0f;
        }
    };
    /**
     * Hitscan (Bow) - Arrows instantly travel to their destination. Max lvl: 1
     */
    public static final EnchantmentBuilder HITSCAN = new EnchantmentBuilder("hitscan", Enchantment.Rarity.RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    /**
     * Lightning Bolt (Bow) - Summon lightning on projectile hit, provided it has sky access. Max lvl: 1
     */
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    /**
     * Marked (Bow and Crossbow) - On entity hit: set a marked entity. Subsequent arrows will home in on this entity. Max
     * lvl: 3
     */
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.RARE, EnchantmentTarget.BOW,
            BOTH_HANDS);
    /**
     * Oversized (Bow) - Longer draw time. Projectiles are bigger and faster, with higher damage. Max lvl: 2
     */
    public static final EnchantmentBuilder OVERSIZED = new EnchantmentBuilder("oversized", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.BOW, BOTH_HANDS);

    static {
        OFFENSIVE = List.of(
                BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                        .setPower(20, 50)
                        .addComponent(SupplementaryComponents.BURST_FIRE),
                FRANTIC.makeIncompatible(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK)
                        .setPower(15, 5, 25, 5)
                        .setMaxLevel(3)
                        .addAttribute("frantic_attack_damage", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> entity.hasStatusEffect(StatusEffects.SPEED) ? 0.06 * lvl : 0),
                FRENZY.makeIncompatible(FRANTIC)
                        .setPower(20, 10, 30, 10)
                        .setMaxLevel(3)
                        .addAttribute("frenzy_attack_damage", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_BASE,
                                (entity, stack, lvl) -> lvl * 0.3f * (double) Math.min(0.5f, 1 - entity.getHealth() / entity
                                        .getMaxHealth())),
                HITSCAN.makeIncompatible(OVERSIZED, MARKED)
                        .setPower(20, 50)
                        .addComponent(SupplementaryComponents.HITSCAN),
                LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER, OVERSIZED)
                        .setPower(20, 50)
                        .addComponent(SupplementaryComponents.LIGHTNING_BOLT),
                MARKED.setPower(20, 5, 25, 5)
                        .addCompatibleItems(Items.CROSSBOW, Items.BOW)
                        .setMaxLevel(3)
                        .addComponent(SupplementaryComponents.MARKED),
                OVERSIZED.makeIncompatible(LIGHTNING_BOLT, Enchantments.QUICK_CHARGE)
                        .setPower(20, 5, 30, 10)
                        .addCompatibleItems(Items.CROSSBOW, Items.BOW)
                        .setMaxLevel(2)
                        .addComponent(SupplementaryComponents.OVERSIZED)
                        .addAttribute("oversized_draw_speed", SupplementaryAttributes.DRAW_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> Math.pow(0.75, lvl) - 1)
        );
    }

    /**
     * <hr><center><h1>Utility enchantments</h1></center><hr>
     */
    public static final List<EnchantmentBuilder> UTILITY;
    /**
     * Drill - breaks up to 2 blocks behind broken blocks. Blocks significantly more difficult to break than the current
     * block will be ignored. Slightly decreases mining speed. Max lvl: 1
     */
    public static final EnchantmentBuilder DRILL = new EnchantmentBuilder("drill", Enchantment.Rarity.RARE, EnchantmentTarget.DIGGER,
            MAIN_HAND) {

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            face = Direction.getFacing(-miner.getRotationVector().x, -miner.getRotationVector().y, -miner.getRotationVector().z);
            int x = face == Direction.EAST ? -1 : face == Direction.WEST ? 1 : 0;
            int y = face == Direction.UP ? -1 : face == Direction.DOWN ? 1 : 0;
            int z = face == Direction.SOUTH ? -1 : face == Direction.NORTH ? 1 : 0;
            BlockPos p = pos.add(x, y, z);
            boolean cont = !world.getBlockState(p).isAir();
            if (EnchantHelper.mutedBlockBreak(miner, world, p, world.getBlockState(p), null)
                    && miner instanceof ServerPlayerEntity player) {
                miner.getMainHandStack().damage(1, player.getRandom(), player);
            }
            if (cont && EnchantHelper.mutedBlockBreak(miner, world, (p = p.add(x, y, z)), world.getBlockState(p),
                    null) && miner instanceof ServerPlayerEntity player) {
                miner.getMainHandStack().damage(1, player.getRandom(), player);
            }
        }
    };
    /**
     * Excavation - allows for mining 3x3 areas. Significantly decreases mining speed. Blocks significantly more difficult
     * to break than the current block will be ignored. Max lvl: 1
     */
    public static final EnchantmentBuilder EXCAVATION = new EnchantmentBuilder("excavation", Enchantment.Rarity.RARE,
            EnchantmentTarget.DIGGER, MAIN_HAND) {

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            int x = (face != Direction.EAST && face != Direction.WEST) ? 1 : 0;
            int y = (face != Direction.UP && face != Direction.DOWN) ? 1 : 0;
            int z = (face != Direction.NORTH && face != Direction.SOUTH) ? 1 : 0;
            int extraDamage = 3;
            BlockPos p;
            for (int i = -x; i < x + 1; i++) {
                for (int j = -y; j < y + 1; j++) {
                    for (int k = -z; k < z + 1; k++) {
                        if (EnchantHelper.mutedBlockBreak(miner, world, (p = pos.add(i, j, k)), world.getBlockState(p),
                                state) && extraDamage-- > 0 && miner instanceof ServerPlayerEntity player) {
                            miner.getMainHandStack().damage(1, player.getRandom(), player);
                        }
                    }
                }
            }
        }
    };
    /**
     * Greed - highlights ores around broken blocks in a 5x5 area. Highlights more ores per level. Max lvl: 3
     */
    public static final EnchantmentBuilder GREED = new EnchantmentBuilder("greed", Enchantment.Rarity.RARE, EnchantmentTarget.DIGGER,
            MAIN_HAND) {

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            if (isClient) {
                int count = EnchantmentHelper.getLevel(GREED, item);
                for (BlockPos p : BlockPos.iterateOutwards(pos, 2, 2, 2)) {
                    if (Registries.BLOCK.getEntry(world.getBlockState(p).getBlock()).isIn(ConventionalBlockTags.ORES)) {
                        StaticFadingBlock.addStaticFadingBlock(new Color(0, 0, 0, 0), new Color(250,
                                100, 255, 40), Vec3d.of(p), new Vec3d(1, 1, 1), 500);
                        count--;
                    }
                    if (count == 0) {
                        break;
                    }
                }
            }
        }
    };
    /**
     * Momentum - continually increases mining speed while mining, but boost wears off quickly when inactive. Max lvl: 1
     */
    public static final EnchantmentBuilder MOMENTUM = new EnchantmentBuilder("momentum", Enchantment.Rarity.RARE,
            EnchantmentTarget.DIGGER, MAIN_HAND) {

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            if (!isClient && !miner.isCreative() && miner.getBlockBreakingSpeed(state) > 1) {
                SupplementaryComponents.MOMENTUM.maybeGet(miner).ifPresent((component) -> component.setValue(30));
            }
        }
    };
    /**
     * Pickup - boomerangs can pick up items, with larger inventories based on level. Max lvl: 3
     */
    public static final EnchantmentBuilder PICKUP = new EnchantmentBuilder("pickup", Enchantment.Rarity.UNCOMMON,
            null, BOTH_HANDS);

    static {
        UTILITY = List.of(
                DRILL.setPower(27, 30)
                        .setMaxLevel(1)
                        .addAttribute("drill_mining_speed", SupplementaryAttributes.EFFECTIVE_MINING_SPEED,
                                Operation.MULTIPLY_TOTAL, (entity, stack, lvl) -> -0.15),
                EXCAVATION.setPower(27, 30)
                        .setMaxLevel(1)
                        .addAttribute("excavation_mining_speed", SupplementaryAttributes.EFFECTIVE_MINING_SPEED,
                                Operation.MULTIPLY_TOTAL, (entity, stack, lvl) -> -0.85),
                GREED.setPower(22, 4, 28, 4)
                        .setMaxLevel(3),
                MOMENTUM.setPower(18, 50)
                        .setMaxLevel(1)
                        .addAttribute("momentum_mining_speed", SupplementaryAttributes.EFFECTIVE_MINING_SPEED,
                                Operation.MULTIPLY_TOTAL, (entity, stack, lvl) -> {
                                    MutableDouble bonus = new MutableDouble(0);
                                    SupplementaryComponents.MOMENTUM.maybeGet(entity).ifPresent((component) -> bonus.add(
                                            ((int) component.getCustom()) / 100.0d));
                                    return Math.min(bonus.getValue(), 1.5);
                                }),
                PICKUP.setPower(10, 10, 20, 20)
                        .setMaxLevel(3)
                        .addCompatibleClasses(BoomerangItem.class)
        );
    }

    /**
     * <hr><center><h1>Mobility enchantments</h1></center><hr>
     * These are various enchantments that all increase user mobility in some way.
     */
    public static final List<EnchantmentBuilder> MOBILITY;
    /**
     * Acrobatic - Boots: wall cling/jump while sneaking and double jump. Max lvl: 1
     */
    public static final EnchantmentBuilder ACROBATIC = new EnchantmentBuilder("acrobatic", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * Air Dash - Boots: dash in the air on double-tapping forward. Max lvl: 1
     */
    public static final EnchantmentBuilder AIR_DASH = new EnchantmentBuilder("air_dash", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * All Terrain - Boots: increased step height, and ability to walk on water. Max lvl: 1
     */
    public static final EnchantmentBuilder ALL_TERRAIN = new EnchantmentBuilder("all_terrain", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * Assault Dash - Shields: charge forward while holding shield up, knocking entities away. Max lvl: 2
     */
    public static final EnchantmentBuilder ASSAULT_DASH = new EnchantmentBuilder("assault_dash", Enchantment.Rarity.VERY_RARE,
            null, BOTH_HANDS);
    /**
     * Bunnyhop - Boots: decrease height, increase speed on jump. Scale small vertical gaps in the air. Max Lvl: 1
     */
    public static final EnchantmentBuilder BUNNYHOP = new EnchantmentBuilder("bunnyhop", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * Grappling - Bows, crossbows, fishing rods: projectiles pull the user in. Varies by tool type. Max lvl: 1
     */
    public static final EnchantmentBuilder GRAPPLING = new EnchantmentBuilder("grappling", Enchantment.Rarity.UNCOMMON,
            EnchantmentTarget.CROSSBOW, MAIN_HAND);
    /**
     * Mole - automatically fit into one-block gaps. Increases crawl speed by 70% per level. Max lvl: 3
     */
    public static final EnchantmentBuilder MOLE = new EnchantmentBuilder("mole", Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_LEGS,
            ALL_ARMOR);
    /**
     * Slimed - Boots: become bouncy, experience much less friction. Max lvl: 1
     */
    public static final EnchantmentBuilder SLIMED = new EnchantmentBuilder("slimed", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * Strafe - Boots: double tap the left/right movement keys to strafe left or right while grounded, swimming, or flying.
     * Max lvl: 1
     */
    public static final EnchantmentBuilder STRAFE = new EnchantmentBuilder("strafe", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_FEET, ALL_ARMOR);
    /**
     * Warp - teleport to arrow location. Max lvl: 1
     */
    public static final EnchantmentBuilder WARP = new EnchantmentBuilder("warp", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.BOW,
            BOTH_HANDS);

    public static final EnchantmentBuilder[] MOBILITY_INCOMPATIBILITY_GROUP = {ACROBATIC, AIR_DASH, ALL_TERRAIN, BUNNYHOP, SLIMED};
    static {
        MOBILITY = List.of(
                ACROBATIC.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1)
                        .setCustomAnvilCost(4),
                AIR_DASH.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1)
                        .setCustomAnvilCost(4),
                ALL_TERRAIN.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1)
                        .setCustomAnvilCost(2),
                ASSAULT_DASH.setPower(15, 15, 30, 15)
                        .setMaxLevel(2)
                        .setCustomAnvilCost(8)
                        .addCompatibleClasses(ShieldItem.class),
                BUNNYHOP.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1)
                        .addAttribute("bunnyhop_move_speed", SupplementaryAttributes.NO_FOV_MOVE_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> -0.15),
                GRAPPLING.makeIncompatible(Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT)
                        .setPower(20,50)
                        .setMaxLevel(1)
                        .addComponent(SupplementaryComponents.GRAPPLING)
                        .setTreasure(true)
                        .addCompatibleClasses(FishingRodItem.class, BowItem.class)
                        .addCompatibleItems(Items.CROSSBOW),
                MOLE.setPower(18, 6, 28, 7)
                        .setMaxLevel(3)
                        .addAttribute("mole_move_speed", SupplementaryAttributes.NO_FOV_MOVE_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> entity.isCrawling() ? lvl * 0.7 : 0),
                SLIMED.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1),
                STRAFE.makeIncompatible(MOBILITY_INCOMPATIBILITY_GROUP)
                        .setPower(20, 50)
                        .setMaxLevel(1),
                WARP.setPower(25, 50)
                        .setMaxLevel(1)
                        .addComponent(SupplementaryComponents.WARP)
        );
    }

    /**
     * <hr><center><h1>Curses</h1></center><hr>
     * Various curses with negative drawbacks. If enough curses are present, contracts can be applied. Vanilla curses can
     * also contribute to this.
     */
    public static final List<EnchantmentBuilder> CURSES;
    /**
     * Atrophy - Melee attacks and bow drawing charge 10% slower per level. Max level: 3. Curse points: 1 per lvl
     */
    public static final CurseBuilder ATROPHY = new CurseBuilder("atrophy", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl);
    /**
     * Backlash - Melee attacks incur a backlash of 1 damage per level, ignoring armor, enchantments, and resistance. Max
     * level: 2. Curse points: 3 per lvl
     */
    public static final CurseBuilder BACKLASH = new CurseBuilder("backlash", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl * 2) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            user.damage(EnchantHelper.backlash(user.getWorld()), Math.min(level, 2));
            return 0.0f;
        }
    };
    /**
     * Blighted - Disables natural health regeneration. At level 2, effect lasts until death. Max lvl: 2. Curse points: 3
     * (lvl 1), 8 (lvl 2)
     */
    public static final CurseBuilder BLIGHTED = new CurseBuilder("blighted", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl > 1 ? 8 : 3);
    /**
     * Fragility - Take (10 + 5 * level)% more damage from entities and projectiles. Max level: 5. Curse points: 1 per lvl
     */
    public static final CurseBuilder FRAGILITY = new CurseBuilder("fragility", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, lvl -> lvl) {
        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            return 1.0f + 0.1f * level;
        }
    };
    /**
     * Fatigue - Repeated mining or attacking slows mining and attack speed. Max lvl: 2
     */
    public static final CurseBuilder FATIGUE = new CurseBuilder("fatigue", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl * 2) {

        @Override
        public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {
            if (!isClient && !miner.isCreative()) {
                SupplementaryComponents.FATIGUE.maybeGet(miner).ifPresent((component) -> component.setValue(200));
            }
        }

        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            SupplementaryComponents.FATIGUE.maybeGet(user).ifPresent((component) -> component.setValue(300));
            return 0.0f;
        }
    };
    /**
     * Gluttony - Exhaustion/hunger accumulates 30% faster per level. Saturation is decreased by 30%. Max lvl: 2. Curse
     * points: 2 * lvl - 1
     */
    public static final CurseBuilder GLUTTONY = new CurseBuilder("gluttony", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> lvl * 2 - 1);
    /**
     * Haunted - Phantoms spawn at all times of day and are more aggressive to wearers. Phantoms may randomly spawn
     * underground. Damage dealt to phantoms is halved, and phantoms spawned via this effect are immune to fire damage.
     * Max lvl: 1. Curse points: 2
     */
    public static final CurseBuilder HAUNTED = new CurseBuilder("haunted", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> 2) {
        @Override
        public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
            damageAmount = target instanceof PhantomEntity ? -damageAmount / 2 : 0;
            return damageAmount;
        }
    };
    /**
     * Moody - Attack damage and mining speed randomly fluctuate from anywhere between -50% to 50% every 5-10 seconds. Max
     * lvl: 1. Curse points: 1.
     */
    public static final CurseBuilder MOODY = new CurseBuilder("moody", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, lvl -> 1);

    static {
        CURSES = List.of(
                ATROPHY.setPower(20, 5, 50, 5)
                        .setMaxLevel(3)
                        .setCustomAnvilCost(1)
                        .addAttribute("atrophy_attack_speed", EntityAttributes.GENERIC_ATTACK_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> lvl * -0.1)
                        .addAttribute("atrophy_draw_speed", SupplementaryAttributes.DRAW_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> lvl * -0.1),
                BACKLASH.setPower(20, 10, 50, 10)
                        .setMaxLevel(2)
                        .setCustomAnvilCost(2),
                BLIGHTED.setPower(20, 10, 50, 10)
                        .setMaxLevel(2)
                        .setCustomAnvilCost(2),
                FATIGUE.setPower(20, 10, 50, 10)
                        .setMaxLevel(2)
                        .setCustomAnvilCost(2)
                        .addAttribute("fatigue_mining_speed", SupplementaryAttributes.MINING_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> {
                                    MutableDouble bonus = new MutableDouble(0);
                                    SupplementaryComponents.FATIGUE.maybeGet(entity).ifPresent((component) -> bonus.add(
                                            -((int) component.getCustom()) / 100.0d));
                                    return Math.max(bonus.getValue(), -0.25 - 0.15 * lvl);
                                })
                        .addAttribute("fatigue_attack_speed", EntityAttributes.GENERIC_ATTACK_SPEED, Operation.
                                MULTIPLY_TOTAL, (entity, stack, lvl) -> {
                                    MutableDouble bonus = new MutableDouble(0);
                                    SupplementaryComponents.FATIGUE.maybeGet(entity).ifPresent((component) -> bonus.add(
                                            -((int) component.getCustom()) / 100.0d));
                                    return Math.max(bonus.getValue(), -0.25 - 0.15 * lvl);
                                }),
                FRAGILITY.setPower(20, 2, 50, 2)
                        .setMaxLevel(5)
                        .setCustomAnvilCost(1),
                GLUTTONY.setPower(20, 10, 50, 10)
                        .setMaxLevel(2)
                        .setCustomAnvilCost(2),
                HAUNTED.setPower(20, 10, 50, 10)
                        .setMaxLevel(1)
                        .setCustomAnvilCost(4)
                        .addComponent(SupplementaryComponents.HAUNTED),
                MOODY.setPower(20, 5, 50, 5)
                        .setMaxLevel(1)
                        .setCustomAnvilCost(1)
                        .addComponent(SupplementaryComponents.MOODY)
                        .addAttribute("moody_attack_damage", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> {
                                    MutableDouble mult = new MutableDouble(0.0);
                                    SupplementaryComponents.MOODY.maybeGet(entity).ifPresent(component -> {
                                        if (component.getCustom() instanceof float[] arr) {
                                            float progress = (entity.getWorld().getTime() - arr[2]) / (arr[3] - arr[2]);
                                            float smoothed = arr[0] + GenMathHelper.smoothstep(progress) * (arr[1] - arr[0]);
                                            mult.setValue(smoothed / 300);
                                        }
                                    });
                                    return mult.getValue();
                                })
                        .addAttribute("moody_mining_speed", SupplementaryAttributes.MINING_SPEED, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> {
                                    MutableDouble mult = new MutableDouble(0.0);
                                    SupplementaryComponents.MOODY.maybeGet(entity).ifPresent(component -> {
                                        if (component.getCustom() instanceof float[] arr) {
                                            float progress = (entity.getWorld().getTime() - arr[2]) / (arr[3] - arr[2]);
                                            float smoothed = arr[0] + GenMathHelper.smoothstep(progress) * (arr[1] - arr[0]);
                                            mult.setValue(smoothed / 300);
                                        }
                                    });
                                    return mult.getValue();
                                })
        );
    }

    /**
     * <hr><center><h1>Contracts</h1></center><hr>
     * Powerful effects that can only be applied if an item has enough curses on it. None are obtainable via random
     * enchantment - they must be applied via anvil.
     */
    public static final List<EnchantmentBuilder> CONTRACTS;
    /**
     * Adaptability - Taking damage grants Regeneration, Resistance if Regeneration is already present, Strength if Resistance
     * is already present, or Speed if Strength is already present, for 8 seconds each. If damage is taken below half health,
     * gives Absorption and Speed II, for 30 and 5 seconds respectively. This effect has a 5 minute cooldown. Slight damage
     * reduction the lower the item durability, up to 15%. Also halves Backlash damage. Curse points required: 8 <br>
     */
    public static final ContractBuilder ADAPTABILITY = new ContractBuilder("adaptability", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, ContractBuilder.NO_OP, 7) {
        @Override
        public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
            float mult = source.isOf(EnchantHelper.BACKLASH) ? 0.5f : 1.0f;
            if (user.hasStatusEffect(StatusEffects.STRENGTH)) {
                user.addStatusEffect(new StatusEffectInstance( StatusEffects.SPEED, 160, 0, false, false));
            }
            if (user.hasStatusEffect(StatusEffects.RESISTANCE)) {
                user.addStatusEffect(new StatusEffectInstance( StatusEffects.STRENGTH, 160, 0, false, false));
            }
            if (user.hasStatusEffect(StatusEffects.REGENERATION)) {
                user.addStatusEffect(new StatusEffectInstance( StatusEffects.RESISTANCE, 160, 0, false, false));
            }
            if (!user.hasStatusEffect(StatusEffects.REGENERATION)) {
                user.addStatusEffect(new StatusEffectInstance( StatusEffects.REGENERATION, 160, 0, false, false));
            }
            SupplementaryComponents.ADAPTABILITY.maybeGet(user).ifPresent(component -> {
                if (component.getValue() <= 0) {
                    if (user.getHealth() < user.getMaxHealth() * 0.5f) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 1, false, false));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 1, false, false));
                    }
                    component.setValue(6000);
                }
            });
            return mult * (1.0f - (float) itemStack.getDamage() / itemStack.getMaxDamage() * 0.15f);
        }
    };
    /**
     * Cursed Power - Increases all melee damage dealt by 10% per curse point, with a penalty of 20% per contract present.
     * Curse points required: 7 <br>
     */
    public static final ContractBuilder CURSED_POWER = new ContractBuilder("cursed_power", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, ContractBuilder.NO_OP, 7);
    /**
     * Magic Guard - Negates many forms of indirect (not from living entities or self) damage entirely. Requires any level
     * of Fragility. Curse points required: 6 <br>
     */
    public static final ContractBuilder MAGIC_GUARD = new ContractBuilder("magic_guard", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.ARMOR_CHEST, ALL_ARMOR, stack -> EnchantmentHelper.getLevel(FRAGILITY, stack) > 0, 5);
    /**
     * Vigor - Increases health by 10 points/5 hearts. Prevents fatal damage once per life, but health bonus is removed
     * until next death. Curse points required: 8 <br>
     */
    public static final ContractBuilder VIGOR = new ContractBuilder("vigor", Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST,
            ALL_ARMOR, ContractBuilder.NO_OP, 8);

    static {
        CONTRACTS = List.of(
                ADAPTABILITY.setPower(50, 50)
                        .setCustomAnvilCost(0),
                CURSED_POWER.setPower(50, 50)
                        .setCustomAnvilCost(0)
                        .addAttribute("cursed_power_damage_bonus", EntityAttributes.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL,
                                (entity, stack, lvl) -> (EnchantHelper.getCursePoints(stack, -2) - 2) * 0.1),
                MAGIC_GUARD.setPower(50, 50)
                        .setCustomAnvilCost(0),
                VIGOR.setPower(50, 50)
                        .setCustomAnvilCost(0)
                        .addAttribute("vigor_health_bonus", EntityAttributes.GENERIC_MAX_HEALTH, Operation.ADDITION,
                                (entity, stack, lvl) -> {
                                    AtomicBoolean vigorRevive = new AtomicBoolean(false);
                                    SupplementaryComponents.VIGOR.maybeGet(entity).ifPresent(component -> vigorRevive.set((Boolean) component.getValue()));
                                    return vigorRevive.get() ? 0 : 10.0;
                                })
        );
    }

    /**
     * Sets enchantment attributes and registers enchantments. Called from {@link Supplementary#onInitialize()}. See
     * {@link EnchantmentBuilder} for specific builder methods.
     */
    public static void register() {
        GENERAL.forEach(EnchantmentBuilder::build);
        UTILITY.forEach(EnchantmentBuilder::build);
        OFFENSIVE.forEach(EnchantmentBuilder::build);
        MOBILITY.forEach(EnchantmentBuilder::build);
        CURSES.forEach(EnchantmentBuilder::build);
        CONTRACTS.forEach(EnchantmentBuilder::build);
    }
}
