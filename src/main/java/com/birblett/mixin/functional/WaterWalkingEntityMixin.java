package com.birblett.mixin.functional;

import com.birblett.lib.mixinterface.BlockCollisionSpliteratorInterface;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public class WaterWalkingEntityMixin {
    /*
    Enables custom water collision behavior for entities with Water Walking enchant
     */

    @Unique private static Entity supplementary$CollidingEntity;
    @Unique private static Vec3d supplementary$Movement;
    @Unique private static Box supplementary$BoundingBox;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"))
    private static void getCollidingEntity(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir) {
        supplementary$CollidingEntity = entity;
        supplementary$Movement = movement;
        supplementary$BoundingBox = entityBoundingBox;
    }

    @ModifyArg(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 1))
    private static Iterable<VoxelShape> modifyCollision(Iterable<VoxelShape> elements) {
        if (supplementary$CollidingEntity instanceof LivingEntity livingEntity && !livingEntity.isTouchingWater() &&
                EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.WATER_WALKING, livingEntity) > 0 &&
                !livingEntity.isSneaking()) {
            BlockCollisionSpliterator blockCollisionSpliterator = new BlockCollisionSpliterator(livingEntity.getWorld(), livingEntity, supplementary$BoundingBox.stretch(supplementary$Movement));
            ((BlockCollisionSpliteratorInterface) blockCollisionSpliterator).setWaterWalking();
            elements = () -> blockCollisionSpliterator;
        }
        return elements;
    }
}
