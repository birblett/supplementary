package com.birblett.lib.components;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static com.birblett.Supplementary.MODID;

/**
 * Provides a simple component attachable to an entity storing a single data value. Generic parameter determines type of
 * data to be stored.
 * @param <T> Type of stored value. Only Integer, Float, and String will be stored as NBT.
 */
public class SimpleEntityComponent<T> implements SimpleComponent<T> {

    private T value;
    private final String key;

    /**
     * @param key The NBT key for this component to be stored under, in the format "supplementary:key"
     */
    public SimpleEntityComponent(String key) {
        this.key = MODID + ":" + key;
    }

    /**
     * @return Stored value of this component
     */
    @Override
    public T getValue() {
        return this.value;
    }

    /**
     * Sets the stored value of this component
     * @param value Provided value with expected type matching this component's type contract
     */
    @Override
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Attemps to read value from NBT.
     * @param tag NBT of attached entity
     */
    @Override @SuppressWarnings("unchecked")
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (this.value != null) {
            Class<T> type = (Class<T>) this.value.getClass();
            if (Integer.class.equals(type)) {
                this.value = type.cast(tag.getInt(key));
            } else if (String.class.equals(type)) {
                this.value = type.cast(tag.getString(key));
            } else if (Float.class.equals(type)) {
                this.value = type.cast(tag.getFloat(key));
            }
        }
    }

    /**
     * Stores value in NBT if value is of a valid type.
     * @param tag NBT of attached entity
     */
    @Override @SuppressWarnings("unchecked")
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.value != null) {
            Class<T> type = (Class<T>) this.value.getClass();
            if (Integer.class.equals(type)) {
                tag.putInt(key, (Integer) this.value);
            } else if (Float.class.equals(type)) {
                tag.putFloat(key, (Float) this.value);
            } else if (String.class.equals(type)) {
                tag.putString(key, (String) this.value);
            }
        }
    }
}
