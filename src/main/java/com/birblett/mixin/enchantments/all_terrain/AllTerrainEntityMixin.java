package com.birblett.mixin.enchantments.all_terrain;

import com.birblett.lib.accessor.BlockCollisionSpliteratorInterface;
import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
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

/**
 * Adjusts step height for All Terrain enchant
 */
@Mixin(Entity.class)
public class AllTerrainEntityMixin {

    @Unique private double stepHeightboost;
    @Unique private static Entity supplementary$CollidingEntity;
    @Unique private static Vec3d supplementary$Movement;
    @Unique private static Box supplementary$BoundingBox;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void adjustStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if ((Entity) (Object) this instanceof LivingEntity self) {
            this.stepHeightboost = EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ALL_TERRAIN, self) * 0.5;
            if (SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ALL_TERRAIN, self) > 0) {
                this.stepHeightboost += 0.1;
            }
            if (this.stepHeightboost > 0) {
                ((Entity) (Object) this).stepHeight += this.stepHeightboost;
            }
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void returnCalcStepHeightAdjustment(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.stepHeightboost > 0) {
            ((Entity) (Object) this).stepHeight -= this.stepHeightboost;
            this.stepHeightboost = 0;
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"))
    private void tailCalcStepHeightAdjustment(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.stepHeightboost > 0) {
            ((Entity) (Object) this).stepHeight -= 0.5 * this.stepHeightboost;
        }
    }

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
                EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ALL_TERRAIN, livingEntity) > 0 &&
                !livingEntity.isSneaking()) {
            BlockCollisionSpliterator blockCollisionSpliterator = new BlockCollisionSpliterator(livingEntity.getWorld(), livingEntity, supplementary$BoundingBox.stretch(supplementary$Movement));
            int collisionType = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ALL_TERRAIN, livingEntity) > 0 ? 2 : 1;
            ((BlockCollisionSpliteratorInterface) blockCollisionSpliterator).extendCollisionConditions(collisionType);
            elements = () -> blockCollisionSpliterator;
        }
        return elements;
    }
}
