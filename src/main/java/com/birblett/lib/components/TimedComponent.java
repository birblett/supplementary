package com.birblett.lib.components;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
        return ticksLeft;
    }

    @Override
    public void setValue(int value) {
        ticksLeft = value;
    }

    @Override
    public void inBlockTick(PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void preEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    @Override
    public void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {}

    @Override
    public void onProjectileRender(PersistentProjectileEntity persistentProjectileEntity, float tickDelta, MatrixStack matrixStack,
                                   VertexConsumerProvider vertexConsumerProvider, int level) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @Override
    public Vec3d onTravel(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {
        return null;
    }

    @Override
    public void decrement() {
        this.ticksLeft--;
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
