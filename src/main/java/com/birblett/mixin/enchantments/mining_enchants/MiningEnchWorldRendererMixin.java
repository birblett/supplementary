package com.birblett.mixin.enchantments.mining_enchants;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders extended outlines for Drill and Excavation enchants, and also adds a world event to spawn block break particles
 * without a block break sound.
 */
@Mixin(WorldRenderer.class)
public abstract class MiningEnchWorldRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyArg(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawCuboidShapeOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDFFFF)V"),
            index = 2)
    private VoxelShape excavatorShapeOverride(VoxelShape vs, @Local Entity entity, @Local BlockPos pos) {
        if (this.client.player != null && this.world != null && this.client.crosshairTarget instanceof BlockHitResult blockHitResult) {
            if (EnchantmentHelper.getLevel(SupplementaryEnchantments.EXCAVATION, this.client.player.getMainHandStack()) > 0) {
                Direction d = blockHitResult.getSide();
                int x = (d != Direction.EAST && d != Direction.WEST) ? 1 : 0;
                int y = (d != Direction.UP && d != Direction.DOWN) ? 1 : 0;
                int z = (d != Direction.NORTH && d != Direction.SOUTH) ? 1 : 0;
                BlockState baseState = this.world.getBlockState(pos);
                for (int i = -x; i < x + 1; i++) {
                    for (int j = -y; j < y + 1; j++) {
                        for (int k = -z; k < z + 1; k++) {
                            BlockPos p = pos.add(i, j, k);
                            BlockState bs = this.world.getBlockState(p);
                            if (EnchantHelper.isValidForBatchMining(this.client.player, this.world, pos, bs, baseState)) {
                                vs = VoxelShapes.union(vs, bs.getOutlineShape(world, p, ShapeContext.of(entity)).offset(i, j, k));
                            }
                        }
                    }
                }
            }
            if (EnchantmentHelper.getLevel(SupplementaryEnchantments.DRILL, this.client.player.getMainHandStack()) > 0) {
                Direction d = Direction.getFacing(-this.client.player.getRotationVector().x, -this.client.player.getRotationVector()
                        .y, -this.client.player.getRotationVector().z);
                int x = d == Direction.EAST ? -1 : d == Direction.WEST ? 1 : 0;
                int y = d == Direction.UP ? -1 : d == Direction.DOWN ? 1 : 0;
                int z = d == Direction.SOUTH ? -1 : d == Direction.NORTH ? 1 : 0;
                BlockState baseState = this.world.getBlockState(pos);
                BlockPos p;
                BlockState bs = this.world.getBlockState(p = pos.add(x, y, z));
                if (EnchantHelper.isValidForBatchMining(this.client.player, this.world, pos, bs, baseState)) {
                    vs = VoxelShapes.union(vs, bs.getOutlineShape(world, p, ShapeContext.of(entity)).offset(x, y, z));
                }
                if (!bs.isAir()) {
                    bs = this.world.getBlockState(p = p.add(x, y, z));
                    if (EnchantHelper.isValidForBatchMining(this.client.player, this.world, pos, bs, baseState)) {
                        vs = VoxelShapes.union(vs, bs.getOutlineShape(world, p, ShapeContext.of(entity)).offset(x + x,
                                y + y, z + z));
                    }
                }
            }
        }
        return vs;
    }

    @Inject(method = "processWorldEvent", at = @At("TAIL"))
    private void mutedBlockDestroyedEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (eventId == 2999) {
            this.world.addBlockBreakParticles(pos, Block.getStateFromRawId(data));
        }
    }

}
