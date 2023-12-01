package com.birblett.lib.components;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.birblett.Supplementary.MODID;

/**
 * An implementation of the Cardinal Components ComponentV3, primarily for use with enchantments that require the use of
 * persistent stored data. Implementable methods are called via mixin event hooks and are no-op by default, while the
 * only data written to NBT is a value associated with the initial enchantment level unless otherwise specified. In its
 * most basic implementation, it only stores enchantment levels in attached projectile entities. Individual components
 * are instantiated and registered in {@link com.birblett.registry.SupplementaryComponents}
 */
@SuppressWarnings("unused")
public class EnchantmentComponent implements BaseComponent {

    private final String id;
    private int enchantmentLevel = 0;

    /**
     * Default constructor for components requiring NBT data storage
     * @param id the string id associated with the component being initialized; used to store NBT data
     */
    public EnchantmentComponent(String id) {
        this.id = MODID + ":" + id;
    }

    /**
     * Constructor used if an id is not necessary
     */
    public EnchantmentComponent() {
        this.id = null;
    }

    /**
     * Implementation of {@link BaseComponent#getValue()}. Returns current stored enchantment level.
     *
     * @return stored enchantment level
     */
    @Override
    public int getValue() {
        return this.enchantmentLevel;
    }

    /**
     * Implementation of {@link BaseComponent#setValue(int)}. Sets the stored enchantment level.
     *
     * @param level integer to set stored level to
     */
    @Override
    public void setValue(int level) {
        this.enchantmentLevel = level;
    }

    /**
     * Implementation of {@link BaseComponent#decrement()}. Decrements the stored enchantment level by 1.
     */
    @Override
    public void decrement() {
        this.enchantmentLevel--;
    }

    /**
     * Implementation of {@link BaseComponent#increment()}. Increments the stored enchantment level by 1.
     */
    @Override
    public void increment() {
        this.enchantmentLevel++;
    }

    /**
     * Implementation of {@link BaseComponent#onProjectileFire(LivingEntity, ProjectileEntity, int, ItemStack, ItemStack)}. Sets the provided
     * projectile's stored enchantment level to the provided level.
     * @param user entity from which projectile is being fired
     * @param projectileEntity  fired projectile entity
     * @param level provided enchantment level
     * @param item item projectile is being fired from
     * @param projectileItem item corresponding to the projectile
     */
    @Override
    public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level, ItemStack item, ItemStack projectileItem) {
        this.setValue(level);
    }

    /**
     * Default implementation of {@link dev.onyxstudios.cca.api.v3.component.Component#readFromNbt(NbtCompound)}.
     * Determines what data is read from NBT. By default, only reads the enchantment level.
     *
     * @param tag provided NBT object to read from
     */
    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (this.id != null) {
            this.enchantmentLevel = tag.getInt(this.id);
        }
    }

    /**
     * Default implementation of {@link dev.onyxstudios.cca.api.v3.component.Component#writeToNbt(NbtCompound)}.
     * Determines what data is stored to NBT. By default, only stores the enchantment level.
     *
     * @param tag provided NBT object to write to
     */
    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.id != null) {
            tag.putInt(this.id, this.enchantmentLevel);
        }
    }
}
