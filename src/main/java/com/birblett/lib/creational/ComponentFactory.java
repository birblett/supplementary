package com.birblett.lib.creational;

import com.birblett.lib.components.SimpleEntityComponent;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.birblett.Supplementary.MODID;

public class ComponentFactory {

    public enum simpleComponentType {
        INTEGER,
        STRING,
        FLOAT
    }

    @NotNull
    public static SimpleEntityComponent<?> simpleEntityComponentFactory(simpleComponentType type, String nbtKey) {
        switch (type) {
            case STRING -> {
                return new SimpleEntityComponent<String>() {
                    String value;
                    final String key = MODID + ":" + nbtKey;

                    @Override
                    public String getValue() {
                        return value;
                    }

                    @Override
                    public void setValue(String value) {
                        this.value = value;
                    }

                    @Override
                    public void readFromNbt(@NotNull NbtCompound tag) {
                        this.value = tag.getString(key);
                    }

                    @Override
                    public void writeToNbt(@NotNull NbtCompound tag) {
                        tag.putString(key, value);
                    }
                };
            }
            case FLOAT -> {
                return new SimpleEntityComponent<Float>() {
                    Float value;
                    final String key = MODID + ":" + nbtKey;

                    @Override
                    public Float getValue() {
                        return value;
                    }

                    @Override
                    public void setValue(Float value) {
                        this.value = value;
                    }

                    @Override
                    public void readFromNbt(@NotNull NbtCompound tag) {
                        this.value = tag.getFloat(key);
                    }

                    @Override
                    public void writeToNbt(@NotNull NbtCompound tag) {
                        tag.putFloat(key, value);
                    }
                };
            }
            default -> {
                return new SimpleEntityComponent<Integer>() {
                    int value;
                    final String key = MODID + ":" + nbtKey;

                    @Override
                    public Integer getValue() {
                        return value;
                    }

                    @Override
                    public void setValue(Integer value) {
                        this.value = value;
                    }

                    @Override
                    public void readFromNbt(@NotNull NbtCompound tag) {
                        this.value = tag.getInt(key);
                    }

                    @Override
                    public void writeToNbt(@NotNull NbtCompound tag) {
                        tag.putInt(key, value);
                    }
                };
            }
        }
    }
}
