package com.birblett.lib.creational;

import com.birblett.Supplementary;
import com.birblett.lib.components.BaseComponent;
import com.birblett.mixin.attributes.LivingEntityAttributeManagerMixin;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public class EnchantmentBuilder extends Enchantment {

    private final Identifier identifier;

    /**
     * <hr><center><h1>Constructor</h1></center><hr>
     * @param identifier Registry key for this enchantment, registered under "supplementary:identifier".
     * @param weight Enchantment rarity, see {@link net.minecraft.enchantment.Enchantment.Rarity}
     * @param type Valid items to apply to, see {@link EnchantmentTarget}. Set to null to specify a manual override.
     * @param slotTypes Valid slots for effects to be applied. Usually used by
     *                  {@link net.minecraft.enchantment.EnchantmentHelper#getEquipmentLevel(Enchantment, LivingEntity)}
     */

    public EnchantmentBuilder(String identifier, Rarity weight, @Nullable EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.slotTypes = slotTypes;
        this.identifier = new Identifier(Supplementary.MODID, identifier);
    }

    /**
     * <hr><center><h1>Subclasses</h1></center><hr>
     * Used for ease of storage and access of some kinds of data. For now only contains the SupplementaryAttribute record,
     * used to store certain attribute info.
     * @param baseName Display name for attribute instance
     * @param attribute Attribute to modify
     * @param operation Operation type (ADD, MULTIPLY_TOTAL, etc)
     * @param scaling How the modifier should interact with entity, item type, and level
     */

    public record SupplementaryAttribute(String baseName, EntityAttribute attribute, EntityAttributeModifier.Operation operation,
                                         TriFunction<LivingEntity, ItemStack, Integer, Double> scaling) {}


    /**
     * <hr><center><h1>Fields and builder methods</h1></center><hr>
     * Field values determine specific enchant attributes and stats. Builder methods are used to set these values and
     * finally register the enchantment to the enchantment registry. Finalize an EnchantmentBuilder with the
     * {@link EnchantmentBuilder#build()} method.
     */

    private int maxLevel = 1;
    private int minPower = 1;
    private int minPowerScale = 0;
    private int maxPower = 100;
    private int maxPowerScale = 0;
    private int customAnvilCost = -1;
    private boolean isCurse = false;
    private boolean isTreasure = false;
    private boolean availableForOffer = true;
    private boolean availableForRandomSelection = true;
    private final EquipmentSlot[] slotTypes;
    private final List<Item> acceptableItems = new ArrayList<>();
    private final List<Class<?>> acceptableItemClasses = new ArrayList<>();
    private final List<ComponentKey<BaseComponent>> components = new ArrayList<>();
    private final List<Enchantment> incompatibleEnchantments = new ArrayList<>();
    private final List<SupplementaryAttribute> attributes = new ArrayList<>();

     /**
     * Sets max level of enchant.
     * @param maxLevel new max level
     */
    public EnchantmentBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    /**
     * Sets the min and max enchantment power of an enchantment. Calculated based on target level and tool
     * enchantability.
     * @param minPower Minimum power, without accounting for level scaling
     * @param maxPower Maximum power, without accounting for level scaling
     */
    public EnchantmentBuilder setPower(int minPower, int maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
        return this;
    }

    /**
     * Sets enchantment power with level scaling parameters. Calculation during enchantment for min and max powers that
     * levels of enchantments may appear at is as follows: <code>power * (level - 1) * scale</code>
     * @param minPower Minimum power, without accounting for level scaling
     * @param maxPower Maximum power, without accounting for level scaling
     */
    public EnchantmentBuilder setPower(int minPower, int minPowerScale, int maxPower, int maxPowerScale) {
        this.minPowerScale = minPowerScale;
        this.maxPowerScale = maxPowerScale;
        return setPower(minPower, maxPower);
    }

    /**
     * Sets the curse attribute of an enchantment.
     * @param isCurse Whether enchantment is a curse or not
     */
    public EnchantmentBuilder setCurse(boolean isCurse) {
        this.isCurse = isCurse;
        return this;
    }

    /**
     * Sets the treasure attribute of an enchantment.
     * @param isTreasure Whether enchantment is a treasure enchantment or not
     */
    public EnchantmentBuilder setTreasure(boolean isTreasure) {
        this.isTreasure = isTreasure;
        return this;
    }

    /**
     * Sets the availability of the enchantment from various sources
     * @param availableForOffer Whether this enchantment will appear in trade offers of librarian villagers.
     * @param availableForRandomSelection Whether this enchantment will appear in enchantment tables or in loot tables.
     */
    public EnchantmentBuilder setAvailability(boolean availableForOffer, boolean availableForRandomSelection) {
        this.availableForOffer = availableForOffer;
        this.availableForRandomSelection = availableForRandomSelection;
        return this;
    }

    /**
     * Sets other enchantments as incompatible with the current enchant.
     * @param enchantments Any number of enchantment arguments, or an array of enchantments.
     */
    public EnchantmentBuilder makeIncompatible(Enchantment... enchantments) {
        incompatibleEnchantments.addAll(Arrays.asList(enchantments));
        return this;
    }

    /**
     * Makes specific item types compatible with this enchantment. Overrides the default EnchantmentTarget behavior.
     * @param items Any number of Items arguments, or an array of Items.
     */
    public EnchantmentBuilder addCompatibleItems(Item... items) {
        this.acceptableItems.addAll(List.of(items));
        return this;
    }

    /**
     * Makes specific item classes compatible with this enchantment. Useful for cross-compatibility with mods where
     * non-vanilla variants of existing vanilla item types is expected.
     * @param classes Any number of class arguments, or an array of classes.
     */
    public EnchantmentBuilder addCompatibleClasses(Class<?>... classes) {
        List.of(classes).forEach((maybeItem) -> {
            if (Item.class.isAssignableFrom(maybeItem)) {
                this.acceptableItemClasses.add(maybeItem);
            }
        });
        return this;
    }

    /**
     * Attaches a Cardinal Components ComponentV3 object to this enchantment. Processed via event hooks.
     * @param key Component key of the attached component.
     * @see com.birblett.registry.SupplementaryEvents
     */
    public EnchantmentBuilder addComponent(ComponentKey<BaseComponent> key) {
        this.components.add(key);
        return this;
    }

    /**
     * Adds an associated attribute modifier. Processed in {@link LivingEntityAttributeManagerMixin}
     * @param baseName Name of attribute modifier to be added. Final attribute name depends on equipped slot (i.e. "name_hand")
     * @param attribute Type of the attribute
     * @param operation Operation type i.e. multiply, add, percent
     * @param scaling How attribute modifier value scales with level
     * @see com.birblett.registry.SupplementaryEvents
     */
    public EnchantmentBuilder addAttribute(String baseName, EntityAttribute attribute, EntityAttributeModifier.Operation operation,
                                           TriFunction<LivingEntity, ItemStack, Integer, Double> scaling) {
        this.attributes.add(new SupplementaryAttribute(baseName, attribute, operation, scaling));
        return this;
    }

    /**
     * Set a custom anvil cost that overrides the vanilla anvil cost logic.
     * @param cost Cost per level of the enchantment
     */
    public EnchantmentBuilder setCustomAnvilCost(int cost) {
        this.customAnvilCost = cost;
        return this;
    }

    /**
     * Registers the enchantment to the enchantment registry
     */
    public void build() {
        Registry.register(Registries.ENCHANTMENT, this.identifier, this);
    }

    /**
     * <hr><center><h1>Getters</h1></center><hr>
     * Overridden from {@link Enchantment}
     * <br><br>
     *
     * @return Max level of this enchantment. Defaults to 1 unless otherwise set.
     */
    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }


    /**
     * @return Min power of this enchantment. Defaults to 1 unless otherwise set.
     */
    @Override
    public int getMinPower(int level) {
        return this.minPower + this.minPowerScale * (level - 1);
    }

    /**
     * @return Max power of this enchantment. Defaults to 100 unless otherwise set.
     */
    @Override
    public int getMaxPower(int level) {
        return this.maxPower + this.maxPowerScale * (level - 1);
    }

    /**
     * @return Whether another specified enchantment is compatible with this enchantment.
     */
    @Override
    protected boolean canAccept(Enchantment other) {
        if (incompatibleEnchantments.contains(other)) {
            return false;
        }
        return super.canAccept(other);
    }

    /**
     * Overrides the existing isAcceptableItem implementation completely if custom acceptable items or item classes
     * are defined.
     * @return whether the specified item is a valid item to apply this enchantment to
     */
    @Override
    public boolean isAcceptableItem(ItemStack item) {
        if (this.acceptableItems.isEmpty() && this.acceptableItemClasses.isEmpty()) {
            return super.isAcceptableItem(item);
        }
        else {
            return this.acceptableItems.contains(item.getItem()) || this.acceptableItemClasses.contains(item.getItem().getClass());
        }
    }

    /**
     * @return Whether this enchantment is a curse or not.
     */
    @Override
    public boolean isCursed() {
        return this.isCurse;
    }

    /**
     * @return Whether this enchantment is a treasure enchantment or not.
     */
    @Override
    public boolean isTreasure() {
        return this.isTreasure;
    }

    /**
     * @return Whether this enchantment is available in librarian trade offers or not.
     */
    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return this.availableForOffer;
    }

    /**
     * @return Whether this enchantment is available in the enchantment table or in random loot tables or not.
     */
    @Override
    public boolean isAvailableForRandomSelection() {
        return this.availableForRandomSelection;
    }

    /**
     * @return Whether the specified enchantment has an attached component or not
     */
    public boolean hasComponent() {
        return !this.components.isEmpty();
    }

    /**
     * @return A list of attached components
     */
    public List<ComponentKey<BaseComponent>> getComponents() {
        return this.components;
    }

    /**
     * @return A list of containers for base attribute fields
     */
    public List<SupplementaryAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * @return Custom anvil cost
     */
    public int getCustomAnvilCost() {
        return this.customAnvilCost;
    }

    /**
     * @return Valid slot types
     */
    public EquipmentSlot[] getSlotTypes() {
        return this.slotTypes;
    }

    /**
     * <hr><center><h1>Event handlers</h1></center><hr>
     * These methods are called via event callback. See individual method docstrings for callback locations.
     * <br><br>
     *
     * Called upon initiating a melee attack on another entity, via event hook. May operate via side effects; return
     * 0.0f for no  direct damage modifier.
     * @return A flat (additive) damage modifier.
     * @see com.birblett.registry.SupplementaryEvents#PLAYER_ATTACK_ENCHANT_EVENTS
     */
    public float onAttack(LivingEntity user, Entity target, int level, boolean isCritical, boolean isMaxCharge, float damageAmount) {
        return 0.0f;
    }
    /**
     * Called on projectile creation, via event hook. This includes bows, crossbows, and fishing rods. Operates via side
     * effects. Only called if there is no attached component; otherwise implement
     * {@link BaseComponent#onProjectileFire(LivingEntity, ProjectileEntity, int, ItemStack, ItemStack)}
     * @param user Entity firing the projectile
     * @param projectileEntity Projectile being created
     * @param level Provided enchantment level
     * @param item Item firing the projectile
     * @see com.birblett.registry.SupplementaryEvents#ARROW_FIRED_ENCHANT_EVENTS
     * @see com.birblett.registry.SupplementaryEvents#BOBBER_CAST_ENCHANT_EVENTS
     */
    public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item) {}

    /**
     * The effect of this enchantment on crossbow fire. Called before projectile is instantiated via event hook.
     * Operates via side effects. Only called if there is no attached component; otherwise implement
     * {@link com.birblett.lib.components.EnchantmentComponent#onCrossbowUse(ItemStack, Hand, ItemStack)}
     * @param crossbow Crossbow being fired
     * @param hand Hand being fired from
     * @param savedProjectile ItemStack loaded in the crossbow
     * @see com.birblett.registry.SupplementaryEvents#CROSSBOW_PREFIRE_ENCHANT_EVENTS
     */
    public void onCrossbowUse(ItemStack crossbow, Hand hand, ItemStack savedProjectile) {}

    /**
     * Called when an entity uses (right clicks) with an item, via event hook. Item classes with existing use()
     * implementations may override this.
     * @param user Player attempting to use the item
     * @param hand Current hand containing the item
     * @see com.birblett.registry.SupplementaryEvents#ITEM_USE_ENCHANT_EVENTS
     */
    public void onUse(PlayerEntity user, Hand hand) {}

    /**
     * Called when an entity with this enchant equipped is damaged. May directly modify incoming damage, or operate via
     * side effects. Intended for applying flat damage modifiers.
     * @see com.birblett.registry.SupplementaryEvents#LIVING_ENTITY_ADD_ENCHANT_DAMAGE_EVENTS
     */
    public void onDamage(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {}

    /**
     * Called when an entity with this enchant equipped is damaged. May directly modify incoming damage, or operate via
     * side effects. Intended for applying damage multipliers.
     * @return A float multiplier
     * @see com.birblett.registry.SupplementaryEvents#LIVING_ENTITY_ADD_ENCHANT_DAMAGE_EVENTS
     */
    public float onDamageMultiplier(LivingEntity user, ItemStack itemStack, DamageSource source, int level, MutableFloat damageAmount) {
        return 1.0f;
    }

    /**
     * Called when a block is mined by an item with this enchant.
     * @param world World of block broken
     * @param state Block broken
     * @param pos Position of block broken
     * @param miner Provided player
     * @param item Tool used
     * @param isClient Whether player is in creative or not
     * @see com.birblett.registry.SupplementaryEvents#POSTMINE_ENCHANT_EVENTS
     */
    public void onBlockBreak(World world, BlockState state, BlockPos pos, PlayerEntity miner, ItemStack item, boolean isClient, Direction face) {}
}