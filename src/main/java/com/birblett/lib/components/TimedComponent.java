package com.birblett.lib.components;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import static com.birblett.Supplementary.MODID;

public class TimedComponent implements IntComponent {

    private int ticksLeft = 0;
    private final String id;
    protected ItemStack itemStack = ItemStack.EMPTY;
    protected Hand hand = null;
    protected ItemStack storedProjectile = ItemStack.EMPTY;

    public TimedComponent(String id) {
        this.id = MODID + ":" + id;
    }

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int value) {
        ticksLeft = value;
    }

    @Override
    public void onEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void setStackInHand(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @Override
    public Vec3d onProjectileTick(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {
        return null;
    }

    @Override
    public void increment() {
        ticksLeft++;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.ticksLeft = tag.getInt(this.id);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(this.id, this.ticksLeft);

    }
}
