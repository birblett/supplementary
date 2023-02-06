package com.birblett.lib.builders;

import com.birblett.Supplementary;
import com.birblett.lib.components.LevelComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.include.com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentBuilder extends Enchantment {

    public static final Map<EnchantmentBuilder, TrackedData<Byte>> TRACKED_ARROW_DATA = Maps.newHashMap();

    private int maxLevel = 1;
    private int minPower = 1;
    private int minPowerScale = 0;
    private int maxPower = 1;
    private int maxPowerScale = 0;
    private SupplementaryComponents.ComponentType componentType = SupplementaryComponents.ComponentType.NONE;
    private ComponentKey<LevelComponent> component;
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

    public EnchantmentBuilder makeIncompatible(Enchantment other) {
        incompatibleEnchantments.add(other);
        return this;
    }

    public EnchantmentBuilder makeIncompatible(List<Enchantment> others) {
        incompatibleEnchantments.addAll(others);
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

    public EnchantmentBuilder addComponent(ComponentKey<LevelComponent> key, SupplementaryComponents.ComponentType type) {
        this.componentType = type;
        this.component = key;
        return this;
    }

    public EnchantmentBuilder register() {
        Registry.register(Registry.ENCHANTMENT, this.identifier, this);
        return this;
    }

    public SupplementaryComponents.ComponentType getComponentType() {
        return this.componentType;
    }

    public ComponentKey<LevelComponent> getComponent() {
        return this.component;
    }

    public boolean onProjectileFire(LivingEntity provider, PersistentProjectileEntity projectileEntity, int level) {
        this.getComponent().get(projectileEntity).setValue(level);
        return false;
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
        for (Enchantment e : incompatibleEnchantments) {
            if (e == other) return false;
        }
        return super.canAccept(other);
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