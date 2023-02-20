package com.birblett.lib.components;

import net.minecraft.nbt.NbtCompound;

import static com.birblett.Supplementary.MODID;

public class SimpleIntTrackingComponent implements SimpleEntityComponent<Integer> {
    /*
    A component designed to track a single value.

    Fields
        value - a single tracked integer
            getter: getValue()
            setter: setvalue(int)
     */

    private int value = -1;
    private final String id;

    public SimpleIntTrackingComponent(String id) {
        this.id = MODID + ":" + id;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Integer getObject() {
        return null;
    }

    @Override
    public void setObject(Integer object) {}

    /*
    methods for storing value to NBT - do not call manually!
     */
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getInt(this.id);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(this.id, this.value);
    }
}
