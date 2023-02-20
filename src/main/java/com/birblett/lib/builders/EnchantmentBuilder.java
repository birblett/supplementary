package com.birblett.lib.builders;

import com.birblett.Supplementary;
import com.birblett.lib.components.BaseComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class EnchantmentBuilder extends Enchantment {
    /*
    Extensible builder for enchantments requiring functionality beyond the vanilla class

    Constructor
        identifier - String representing registry namespace
        weight - Enchantment.Rarity passed to superclass constructor
        type - EnchantmentTarget passed to superclass constructor; addCompatibleItems method overrides this behavior.
        slotTypes - EquipmentSlot[] passed to superclass constructor

    Fields
        maxLevel - maximum enchantment level obtainable via enchanting
        minPower - minimum enchantment power required to appear in enchantment pool (for a level 1 enchantment)
        minPowerScale - amount minimum power requirement should scale per additional level
        maxPower - maximum enchantment power required to appear in enchantment pool (for a level 1 enchantment)
        maxPowerScale - amount maximum power requirement should scale per additional level
        components - list of component keys to iterate through when a game event invokes this enchantment
        acceptableTypes - list of acceptable items, overrides the vanilla acceptable item type behavior if set
        acceptableItemClasses - list of acceptable item classes, overrides the vanilla acceptable item behavior if set
        identifier - registry namespace to register under (format: "supplementary:identifier")
        isCurse - whether the enchantment is considered a curse or not
        isTreasure - whether the enchantment is considered a treasure enchantment or not
        availableForOffer - whether the enchantment should show up in villager trade offers or not
        availableForRandomSelection - whether the enchantment should appear in enchantment tables or loot tables
        incompatibleEnchantments - list of enchantments that are not compatible with this enchantment

    Builder methods
        setMaxLevel(int) - setter for maxLevel
        setPower(int, int) - setter for min and max power, assuming neither scale with target level
        setPower(int, int, int, int) - setter for min and max power + scale
        setCurse(boolean) - setter for isCurse
        setTreasure(boolean) - setter for isTreasure
        setAvailability(boolean, boolean) - setter for availableForRandomOffer, availableForRandomSelection
        makeIncompatible(Enchantment...) - makes provided enchantment(s) incompatible with this enchantment
        addCompatibleItems(Item...) - makes provided item(s) compatible with this enchantment
        addCompatibleClasses(Class...) - makes provided item classes compatible with this enchantment
        addComponent(ComponentKey<BaseComponent>) - attaches provided ComponentKey to this enchantment
        build() - builds + registers the enchantment

    Non-inherited methods
        getComponents() - returns components list
     */

    private int maxLevel = 1;
    private int minPower = 1;
    private int minPowerScale = 0;
    private int maxPower = 1;
    private int maxPowerScale = 0;
    private final List<ComponentKey<BaseComponent>> components =
            new ArrayList<>();
    private final List<Item> acceptableItems =
            new ArrayList<>();
    private final List<Class<?>> acceptableItemClasses =
            new ArrayList<>();
    private final Identifier identifier;
    private boolean isCurse = false;
    private boolean isTreasure = false;
    private boolean availableForOffer = true;
    private boolean availableForRandomSelection = true;
    private final List<Enchantment> incompatibleEnchantments = new ArrayList<>();

    public EnchantmentBuilder(String identifier, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.identifier = new Identifier(Supplementary.MODID, identifier);
    }

    public EnchantmentBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public EnchantmentBuilder setPower(int minPower, int maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
        return this;
    }

    public EnchantmentBuilder setPower(int minPower, int minPowerScale, int maxPower, int maxPowerScale) {
        this.minPower = minPower;
        this.minPowerScale = minPowerScale;
        this.maxPower = maxPower;
        this.maxPowerScale = maxPowerScale;
        return this;
    }

    public EnchantmentBuilder setCurse(boolean isCurse) {
        this.isCurse = isCurse;
        return this;
    }

    public EnchantmentBuilder setTreasure(boolean isTreasure) {
        this.isTreasure = isTreasure;
        return this;
    }

    public EnchantmentBuilder setAvailability(boolean availableForOffer, boolean availableForRandomSelection) {
        this.availableForOffer = availableForOffer;
        this.availableForRandomSelection = availableForRandomSelection;
        return this;
    }

    public EnchantmentBuilder makeIncompatible(Enchantment... enchantments) {
        incompatibleEnchantments.addAll(Arrays.asList(enchantments));
        return this;
    }

    public EnchantmentBuilder addCompatibleItems(Item... items) {
        this.acceptableItems.addAll(List.of(items));
        return this;
    }

    public EnchantmentBuilder addCompatibleClasses(Class<?>... classes) {
        this.acceptableItemClasses.addAll(List.of(classes));
        return this;
    }

    public EnchantmentBuilder addComponents(ComponentKey<BaseComponent> key) {
        this.components.add(key);
        return this;
    }

    public List<ComponentKey<BaseComponent>> getComponents() {
        return this.components;
    }

    public void build() {
        Registry.register(Registry.ENCHANTMENT, this.identifier, this);
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public int getMinPower(int level) {
        return this.minPower + this.minPowerScale * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return this.maxPower + this.maxPowerScale * (level - 1);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (incompatibleEnchantments.contains(other)) {
            return false;
        }
        return super.canAccept(other);
    }

    @Override
    public boolean isAcceptableItem(ItemStack item) {
        if (this.acceptableItems.isEmpty() && this.acceptableItemClasses.isEmpty()) {
            return super.isAcceptableItem(item);
        }
        else {
            return this.acceptableItems.contains(item.getItem()) || this.acceptableItemClasses.contains(item.getItem().getClass());
        }
    }

    @Override
    public boolean isCursed() {
        return isCurse;
    }

    @Override
    public boolean isTreasure() {
        return isTreasure;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return this.availableForOffer;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return this.availableForRandomSelection;
    }
}