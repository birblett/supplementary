package com.birblett.entities;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BoomerangEntity extends ProjectileEntity {

    private int age = 0;
    private int canDamageTicks = 0;
    private int returnTicks = 0;
    private boolean shouldReturn = false;
    private boolean shouldInsert;
    private ItemStack itemStack = ItemStack.EMPTY;
    private static final TrackedData<Integer> PIERCING = DataTracker.registerData(BoomerangEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(BoomerangEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public BoomerangEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BoomerangEntity(Entity owner, World world, boolean shouldInsert) {
        this(SupplementaryEntities.BOOMERANG, world);
        this.setOwner(owner);
        this.shouldInsert = shouldInsert;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(PIERCING, 0);
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    public void setStack(ItemStack itemStack) {
        this.getDataTracker().set(STACK, itemStack);
    }

    public ItemStack getStack() {
        return this.itemStack;
    }

    public int getAge() {
        return age;
    }

    public void tick() {
        Vec3d prevVelocity = this.getVelocity();
        Vec3d position = this.getPos();
        Vec3d nextPos = position.add(prevVelocity);
        HitResult hitResult = this.world.raycast(new RaycastContext(position, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        // get a non-owner entity collision
        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(this.world, this, this.getPos(), this.getPos()
                .add(prevVelocity), this.getBoundingBox().stretch(prevVelocity).expand(1.0), e -> e != getOwner());
        if (entityHitResult != null && entityHitResult.getEntity().collides()) {
            Entity hit = entityHitResult.getEntity();
            if (!world.isClient()) {
                // ignore up to 5 iframes
                if (hit instanceof LivingEntity livingEntity && livingEntity.hurtTime <= 5) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 0;
                }
                hit.damage(DamageSource.thrownProjectile(this, this.getOwner()), 5.0f);
                if (this.itemStack.damage(1, random, (ServerPlayerEntity) this.getOwner())) {
                    // do item break stuff here
                }
            }
            // reverse velocity towards owner
            if (!this.shouldReturn && this.getOwner() != null) {
                this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).normalize().multiply(-1.2));
            }
            // mark as currently returning
            this.shouldReturn = true;
        }
        if (!shouldReturn) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                if (!this.world.isClient() && this.itemStack.damage(2, random, (ServerPlayerEntity) this.getOwner())) {
                    // do item break stuff here
                }
                if (this.getOwner() != null) {
                    // if has owner, return to owner
                    this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).normalize().multiply(-1.2));
                    this.shouldReturn = true;
                }
                else {
                    // spawn an item of corresponding type if no owner is found
                    Vec3d velocity = this.getVelocity();
                    ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX() - velocity.x, this.getY() - velocity.y,
                            this.getZ() - velocity.z, this.itemStack);
                    itemEntity.setVelocity(this.getVelocity().normalize().multiply(0.1));
                    this.getWorld().spawnEntity(itemEntity);
                    this.kill();
                    this.discard();
                }
            }
            // apply gravity and drag to velocity
            if (!this.hasNoGravity()) {
                this.addVelocity(0.0, -0.01, 0.0);
            }
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getVelocity().lengthSquared() < 0.1) {
                this.shouldReturn = true;
            }
            // apply velocity to pos
        }
        else {
            // vary return speed based on time spent returning, with a cap of 1.0
            double returnSpeed =  0.3 + this.returnTicks / 100.0;
            if (returnSpeed > 1.0) {
                returnSpeed = 1.0;
            }
            // if owner within a certain distance, return to owner
            if (this.getOwner() != null && this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).lengthSquared() < (0.15 * returnSpeed / 0.3)) {
                if (shouldInsert && this.getOwner() instanceof PlayerEntity player) {
                    if (player.getMainHandStack() == ItemStack.EMPTY) {
                        player.setStackInHand(Hand.MAIN_HAND, this.itemStack);
                    }
                    else if (player.getOffHandStack() == ItemStack.EMPTY) {
                        player.setStackInHand(Hand.OFF_HAND, this.itemStack);
                    }
                    else {
                        player.giveItemStack(this.itemStack);
                    }
                }
                this.kill();
                this.discard();
            }
            this.returnTicks++;
            // move at a constant direction towards owner
            if (this.getOwner() != null) {
                this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.2, 0.0)).normalize().multiply(-returnSpeed));
            }
        }
        Vec3d finalVelocity = this.getVelocity();
        this.setPosition(this.getPos().add(finalVelocity));
        this.velocityModified = true;
        this.age++;
        if (this.canDamageTicks > 0) {
            canDamageTicks--;
        }
        this.itemStack = this.getDataTracker().get(STACK);
        super.tick();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Age", (short)this.age);
        nbt.putShort("ReturnTicks", (short)this.returnTicks);
        nbt.putBoolean("ShouldReturn", this.shouldReturn);
        if (!this.getStack().isEmpty()) {
            nbt.put("Item", this.getStack().writeNbt(new NbtCompound()));
        }
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getShort("Age");
        this.returnTicks = nbt.getShort("ReturnTicks");
        this.shouldReturn = nbt.getBoolean("ShouldReturn");
        NbtCompound nbtCompound = nbt.getCompound("Item");
        this.setStack(ItemStack.fromNbt(nbtCompound));
        if (this.getStack().isEmpty()) {
            this.discard();
        }
        super.readCustomDataFromNbt(nbt);
    }
}
