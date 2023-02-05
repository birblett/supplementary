package com.birblett.lib.components;

import net.minecraft.nbt.NbtCompound;

import static com.birblett.Supplementary.MODID;

public class LevelComponent implements IntComponent {
    private final String id;
    private int enchantmentLevel = 0;

    public LevelComponent(String id) {
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
