package com.birblett.lib.components;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

import static com.birblett.Supplementary.MODID;

public class EnchantmentComponent implements LevelComponent {
    private final String id;
    private int enchantmentLevel = 0;
    private EntityComponent attachedEntityComponent = null;

    public EnchantmentComponent(String id) {
        this.id = MODID + ":" + id;
    }

    @Override
    public int getValue() {
        return this.enchantmentLevel;
    }

    @Override
    public void setValue(int level) {
        this.enchantmentLevel = level;
    }

    @Override
    public void onEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public Vec3d onProjectileTick(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {return velocity;}

    @Override
    public void increment() {
        this.enchantmentLevel++;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.enchantmentLevel = tag.getInt(this.id);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(this.id, this.enchantmentLevel);
    }
}
