package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
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

public class SyncedEnchantmentComponent implements BaseComponent, AutoSyncedComponent {

    private final String id;
    private int enchantmentLevel = 0;

    public SyncedEnchantmentComponent(String id) {
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
    public Entity getEntity() {
        return null;
    }

    @Override
    public void setEntity(Entity entity) {}

    @Override
    public void inBlockTick(PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void preEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void postEntityHit(Entity target, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onHandSwingEvent(LivingEntity entity, Hand hand) {}

    @Override
    public void onUse(LivingEntity entity, Hand hand) {}

    @Override
    public void onBlockHit(BlockHitResult blockHitResult, PersistentProjectileEntity persistentProjectileEntity, int lvl) {}

    @Override
    public void onCrossbowUse(ItemStack stack, Hand hand, ItemStack savedProjectile) {}

    @Override
    public void onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {
        this.setValue(level);
    }

    @Override
    public void onProjectileRender(PersistentProjectileEntity persistentProjectileEntity, float tickDelta, MatrixStack matrixStack,
                                   VertexConsumerProvider vertexConsumerProvider, int level) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @Override
    public Vec3d onTravel(PersistentProjectileEntity persistentProjectile, int level, Vec3d velocity) {return velocity;}

    @Override
    public void decrement() {
        this.enchantmentLevel--;
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
