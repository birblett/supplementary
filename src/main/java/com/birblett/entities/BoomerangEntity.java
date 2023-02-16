package com.birblett.entities;

import com.birblett.registry.SupplementaryEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BoomerangEntity extends ProjectileEntity {

    private int age = 0;
    private int canDamageTicks = 0;
    private int returnTicks = 0;
    private int storedSlot = -100;
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
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    public void setStoredSlot(int slot) {
        this.storedSlot = slot;
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
                .add(prevVelocity), this.getBoundingBox().stretch(prevVelocity).expand(1.0), e -> e != getOwner(), 0.2f);
        if (entityHitResult != null && entityHitResult.getEntity().collides()) {
            Entity entity = entityHitResult.getEntity();
            if (!world.isClient()) {
                // ignore up to 5 iframes
                if (entity instanceof LivingEntity livingEntity && livingEntity.hurtTime <= 5) {
                    livingEntity.hurtTime = 0;
                    livingEntity.timeUntilRegen = 0;
                }
                entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 5.0f);
                // use durability on hit
                if (this.itemStack.damage(1, random, (ServerPlayerEntity) this.getOwner())) {
                    this.world.sendEntityStatus(this, EntityStatuses.BREAK_MAINHAND);
                    this.kill();
                    this.discard();
                }
            }
            // reverse velocity towards owner
            if (!this.shouldReturn && this.getOwner() != null) {
                this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).normalize().multiply(-0.9));
            }
            // mark as currently returning
            this.shouldReturn = true;
        }
        if (!shouldReturn) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // use durability on block hit
                if (!this.world.isClient() && this.itemStack.damage(2, random, (ServerPlayerEntity) this.getOwner())) {
                    this.world.sendEntityStatus(this, EntityStatuses.BREAK_MAINHAND);
                    this.kill();
                    this.discard();
                }
                if (this.getOwner() != null) {
                    // if has owner, return to owner
                    this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).normalize().multiply(-0.9));
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
                    // -99 is magic number representing offhand, as insertStack does not work with offhand slot
                    if (this.storedSlot == -99 && player.getOffHandStack() == ItemStack.EMPTY) {
                        player.setStackInHand(Hand.OFF_HAND, this.itemStack);
                    }
                    else if (!player.getInventory().insertStack(this.storedSlot, this.itemStack)) {
                        player.giveItemStack(this.itemStack);
                    }
                    if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                        serverPlayerEntity.world.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
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
        nbt.putInt("StoredSlot", this.storedSlot);
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
        this.storedSlot = nbt.getInt("StoredSlot");
        NbtCompound nbtCompound = nbt.getCompound("Item");
        this.setStack(ItemStack.fromNbt(nbtCompound));
        if (this.getStack().isEmpty()) {
            this.discard();
        }
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void handleStatus(byte status) {
        /*
        sync break sound and particle effects on client
         */
        switch (status) {
            case EntityStatuses.BREAK_MAINHAND -> {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f, true);
                this.spawnBreakParticles(itemStack);
            }
        }
    }

    private void spawnBreakParticles(ItemStack stack) {
        /*
        spawnItemParticles from LivingEntity, modified
         */
        for (int i = 0; i < 15; ++i) {
            Vec3d particleVelocity = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1,
                    ((double)this.random.nextFloat() - 0.5) * 0.1).normalize().multiply(0.15 * (this.random.nextFloat() + 0.5));
            this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), this.getX(), this.getY(),this.getZ(), particleVelocity.x, particleVelocity.y + 0.05, particleVelocity.z);
        }
    }
}