package com.birblett.lib.components;

import com.birblett.Supplementary;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

import static com.birblett.Supplementary.MODID;

public class SimpleItemStackComponent implements SimpleEntityComponent<ItemStack>, AutoSyncedComponent {

    private ItemStack itemStack = ItemStack.EMPTY;
    private final String id;

    public SimpleItemStackComponent(String id) {
        this.id = MODID + ":" + id;
    }

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int i) {

    }

    @Override
    public ItemStack getObject() {
        return this.itemStack;
    }

    @Override
    public void setObject(ItemStack itemStack) {
        Supplementary.LOGGER.info("{}", itemStack);
        this.itemStack = itemStack;
        Supplementary.LOGGER.info("{}", this.itemStack);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        try {
            itemStack = ItemStack.fromNbt(StringNbtReader.parse(tag.getString(id)));
        } catch (CommandSyntaxException ignored) {}
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound nbtCompound = this.itemStack.getOrCreateNbt();
        nbtCompound.putString("id", "supplementary:" + this.itemStack.getItem().toString());
        tag.putString(id, nbtCompound.asString());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
