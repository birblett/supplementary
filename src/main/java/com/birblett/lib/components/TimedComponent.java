package com.birblett.lib.components;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

import static com.birblett.Supplementary.MODID;

public class TimedComponent implements BaseComponent {

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
    public Entity getEntity() {
        return null;
    }

    @Override
    public void setEntity(Entity entity) {}

    @Override
    public void inBlockTick(ProjectileEntity projectileEntity, int lvl) {}

    @Override
    public void preEntityHit(Entity target, ProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public boolean postEntityHit(Entity target, ProjectileEntity projectileEntity, int lvl) {
        return false;
    }

    @Override
    public void onHandSwingEvent(LivingEntity entity, Hand hand) {}

    @Override
    public void onUse(LivingEntity entity, Hand hand) {}

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, ProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    @Override
    public void onProjectileFire(LivingEntity user, ProjectileEntity projectileEntity, int level) {}

    @Override
    public void onProjectileRender(ProjectileEntity projectileEntity, float tickDelta, MatrixStack matrixStack,
                                   VertexConsumerProvider vertexConsumerProvider, int level) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @Override
    public Vec3d onProjectileTravel(ProjectileEntity projectileEntity, int level, Vec3d velocity) {
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
