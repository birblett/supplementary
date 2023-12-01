package com.birblett.mixin.enchantments.all_terrain;

import com.birblett.lib.accessor.BlockCollisionSpliteratorInterface;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BiFunction;

/**
 * Allows for custom collision behavior with All Terrain
 */
@Mixin(BlockCollisionSpliterator.class)
public class AllTerrainBlockCollisionSpliteratorMixin implements BlockCollisionSpliteratorInterface {

    @Unique private BlockPos supplementary$blockPos;
    @Unique private CollisionView supplementary$collisionView;
    @Unique private BlockState supplementary$blockState;
    @Unique private int supplementary$extendCollision = 0;

    @Override
    public void extendCollisionConditions(int type) {
        this.supplementary$extendCollision = type;
    }

    @SuppressWarnings("rawtypes")
    @Inject(method = "<init>(Lnet/minecraft/world/CollisionView;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;ZLjava/util/function/BiFunction;)V",
            at = @At("TAIL"))
    private void getWorld(CollisionView world, Entity entity, Box box, boolean forEntity, BiFunction resultFunction, CallbackInfo ci) {
        this.supplementary$collisionView = world;
    }

    @Inject(method = "computeNext", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getBlockInfo(CallbackInfoReturnable<VoxelShape> cir, int i, int j, int k, int l, BlockView blockView, BlockState blockState) {
        this.supplementary$blockPos = new BlockPos(i, j, k);
        this.supplementary$blockState = blockState;
    }

    @ModifyVariable(method = "computeNext", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/shape/VoxelShapes;fullCube()Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape replaceWithFluidVoxelShape(VoxelShape voxelShape) {
        if (this.supplementary$extendCollision == 1) {
            if (this.supplementary$blockState.getFluidState().isOf(Fluids.WATER) || this.supplementary$blockState.getFluidState().isOf(Fluids.FLOWING_WATER)) {
                return this.supplementary$blockState.getFluidState().getShape(this.supplementary$collisionView, this.supplementary$blockPos);
            }
        }
        else if (this.supplementary$extendCollision == 2) {
            if (!this.supplementary$blockState.getFluidState().isOf(Fluids.EMPTY)) {
                return this.supplementary$blockState.getFluidState().getShape(this.supplementary$collisionView, this.supplementary$blockPos);
            }
        }
        return voxelShape;
    }
}
