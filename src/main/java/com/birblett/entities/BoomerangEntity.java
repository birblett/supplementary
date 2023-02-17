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
import net.minecraft.nbt.NbtList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoomerangEntity extends ProjectileEntity {

    private int age = 0;
    private int canDamageTicks = 0;
    private int pickupLevel = 0;
    private final List<ItemStack> inventory = new ArrayList<>();
    private ItemStack itemStack = ItemStack.EMPTY;
    private int returnTicks = 0;
    private int storedSlot = -100;
    private float storedAngle;
    private boolean shouldReturn = false;
    private boolean shouldInsert;
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

    public void tick() {
        if (this.age == 0 && this.getOwner() != null) {
            this.storedAngle = -this.getOwner().getHeadYaw() % 360;
        }
        Vec3d prevVelocity = this.getVelocity();
        Vec3d position = this.getPos();
        Vec3d nextPos = position.add(prevVelocity);
        HitResult hitResult = this.world.raycast(new RaycastContext(position, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        // get a non-owner entity collision
        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(this.world, this, this.getPos(), this.getPos()
                .add(prevVelocity), this.getBoundingBox().stretch(prevVelocity).expand(1.0), e -> e != getOwner(), this.shouldReturn ? 0.3f : 0.2f);
        if (entityHitResult != null) {
            if (entityHitResult.getEntity().collides()) {
                Entity entity = entityHitResult.getEntity();
                if (!world.isClient()) {
                    // ignore up to 5 iframes
                    if (entity instanceof LivingEntity livingEntity && livingEntity.hurtTime > 10) {
                        livingEntity.hurtTime = 0;
                        livingEntity.timeUntilRegen = 0;
                    }
                    entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 5.0f);
                    // use durability on hit
                    if (this.itemStack.damage(1, random, (ServerPlayerEntity) this.getOwner())) {
                        this.world.sendEntityStatus(this, EntityStatuses.BREAK_MAINHAND);
                        this.discard();
                    }
                }
                // continue if piercing, otherwise mark as currently returning and reverse velocity
                if (this.dataTracker.get(PIERCING) > 0) {
                    this.dataTracker.set(PIERCING, this.dataTracker.get(PIERCING) - 1);
                }
                else {
                    this.shouldReturn = true;
                    if (this.getOwner() != null) {
                        this.setVelocity(this.getPos().subtract(this.getOwner().getPos().add(0.0, 1.0, 0.0)).normalize().multiply(-0.9));
                    }
                }
            }
            else if (this.pickupLevel > 0 && entityHitResult.getEntity() instanceof ItemEntity item && !this.world.isClient()
                    && this.takeStack(item)) {
                // if enchanted with pickup, store items in inv
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
        if (!shouldReturn) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // use durability on block hit
                if (!this.world.isClient() && this.itemStack.damage(2, random, (ServerPlayerEntity) this.getOwner())) {
                    this.world.sendEntityStatus(this, EntityStatuses.BREAK_MAINHAND);
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
                    // drop inventory items if necessary
                    if (!this.inventory.isEmpty()) {
                        for (ItemStack itemStack : this.inventory) {
                            ItemEntity entity = new ItemEntity(this.getWorld(), this.getX() - velocity.x, this.getY() - velocity.y,
                                    this.getZ() - velocity.z, itemStack);
                            this.getWorld().spawnEntity(entity);
                        }
                    }
                    itemEntity.setVelocity(this.getVelocity().normalize().multiply(0.1));
                    this.getWorld().spawnEntity(itemEntity);
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
                if (this.getOwner() instanceof PlayerEntity player) {
                    this.setPosition(player.getPos());
                    if (this.shouldInsert) {
                        // -99 is magic number representing offhand, as insertStack does not work with offhand slot
                        if (this.storedSlot == -99 && player.getOffHandStack() == ItemStack.EMPTY) {
                            player.setStackInHand(Hand.OFF_HAND, this.itemStack);
                            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                                serverPlayerEntity.world.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                            }
                        }
                        else if (player.getInventory().getStack(this.storedSlot).isEmpty()) {
                            player.getInventory().insertStack(this.storedSlot, this.itemStack);
                            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                                serverPlayerEntity.world.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                            }
                        }
                        else {
                            ItemEntity itemEntity = this.dropStack(this.itemStack, 1.0f);
                            if (itemEntity != null) {
                                itemEntity.setPickupDelay(0);
                            }
                        }
                    }
                    // also give stored inventory to player
                    for (ItemStack itemStack : this.inventory) {
                        ItemEntity itemEntity = this.dropStack(itemStack, 0.1f);
                        if (itemEntity != null) {
                            itemEntity.setPickupDelay(0);
                        }
                    }
                }
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
    protected void initDataTracker() {
        this.dataTracker.startTracking(PIERCING, 0);
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Age", (short)this.age);
        nbt.putShort("ReturnTicks", (short)this.returnTicks);
        nbt.putBoolean("ShouldReturn", this.shouldReturn);
        nbt.putInt("StoredSlot", this.storedSlot);
        if (this.pickupLevel > 0) {
            nbt.putInt("PickupLevel", this.pickupLevel);
            NbtList nbtList = new NbtList();
            for (ItemStack itemStack : this.inventory) {
                nbtList.add(itemStack.writeNbt(new NbtCompound()));
            }
            nbt.put("Inventory", nbtList);
        }
        if (!this.getStack().isEmpty()) {
            nbt.put("Item", this.getStack().writeNbt(new NbtCompound()));
        }
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getShort("Age");
        this.pickupLevel = nbt.getInt("PickupLevel");
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

    public int getAge() {
        return age;
    }

    public void setPickupLevel(int pickupLevel) {
        this.pickupLevel = pickupLevel;
    }

    public void setPierceLevel(int level) {
        this.dataTracker.set(PIERCING, level);
    }

    public void setStack(ItemStack itemStack) {
        this.getDataTracker().set(STACK, itemStack);
    }

    public ItemStack getStack() {
        return this.itemStack;
    }

    public void setStoredSlot(int slot) {
        this.storedSlot = slot;
    }

    public float getStoredAngle() {
        return this.storedAngle;
    }

    public boolean takeStack(ItemEntity itemEntity) {
        ItemStack item = itemEntity.getStack();
        boolean tookStack = false;
        for (ItemStack itemStack : this.inventory) {
            if (item.getItem() == itemStack.getItem() && itemStack.getCount() < itemStack.getMaxCount()) {
                tookStack = true;
                int itemCount = item.getCount();
                int remainingSlots = itemStack.getMaxCount() - itemStack.getCount();
                if (itemCount > remainingSlots) {
                    itemStack.setCount(itemStack.getMaxCount());
                    item.setCount(itemCount - remainingSlots);
                }
                else {
                    itemStack.setCount(itemCount + item.getCount());
                    itemEntity.discard();
                }
            }
        }
        if (!itemEntity.isRemoved() && this.inventory.size() < this.pickupLevel) {
            this.inventory.add(itemEntity.getStack());
            itemEntity.discard();
        }
        return tookStack;
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
