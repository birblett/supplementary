package com.birblett.mixin.enchantments.mining_enchants;

import com.birblett.Supplementary;
import com.birblett.lib.accessor.ServerPlayerInteractionManagerInterface;
import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Block break outlines on the serverside for Drill and Excavation enchants.
 */
@Mixin(ServerPlayerInteractionManager.class)
public class MiningEnchServerPlayerInteractionManagerMixin implements ServerPlayerInteractionManagerInterface {

    @Shadow @Final protected ServerPlayerEntity player;
    @Shadow protected ServerWorld world;
    @Shadow private int blockBreakingProgress;
    @Shadow private BlockPos miningPos;
    @Unique private Direction direction = null;
    @Unique private BlockPos supplementary$LastBreakPos = null;
    @Unique private final List<Pair<Integer, BlockPos>> supplementary$AdditionalBreakPos = new ArrayList<>();

    @Override
    public void setMiningDirection(Direction direction) {
        this.direction = direction;
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void resetBlockBreakProgress(CallbackInfo ci) {
        if (this.supplementary$LastBreakPos == null || this.world.getBlockState(this.supplementary$LastBreakPos).isAir()
                || this.miningPos != this.supplementary$LastBreakPos) {
            this.supplementary$AdditionalBreakPos.forEach((pair -> EnchantHelper.setBlockBreakingInfoExclude(this.player.getId(),
                    pair.getLeft(), pair.getRight(), -1, this.world)));
            this.supplementary$AdditionalBreakPos.clear();
        }
    }

    @Inject(method = "continueMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockBreakingInfo(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void updateBlockBreakProgress(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.EXCAVATION, this.player.getMainHandStack()) > 0) {
            this.supplementary$LastBreakPos = pos;
            if (this.direction == null) {
                if (this.player.raycast(5, 0, false) instanceof BlockHitResult result) {
                    this.direction = result.getSide();
                }
            }
            int x = (this.direction != Direction.EAST && this.direction != Direction.WEST) ? 1 : 0;
            int y = (this.direction != Direction.UP && this.direction != Direction.DOWN) ? 1 : 0;
            int z = (this.direction != Direction.NORTH && this.direction != Direction.SOUTH) ? 1 : 0;
            BlockState baseState = this.world.getBlockState(pos);
            int id = this.player.getId() + 1;
            this.supplementary$AdditionalBreakPos.clear();
            for (int i = -x; i < x + 1; i++) {
                for (int j = -y; j < y + 1; j++) {
                    for (int k = -z; k < z + 1; k++) {
                        BlockPos p = pos.add(i, j, k);
                        BlockState bs = this.world.getBlockState(p);
                        if (EnchantHelper.isValidForBatchMining(this.player, this.world, pos, bs, baseState)) {
                            this.supplementary$AdditionalBreakPos.add(new Pair<>(id++, p));
                        }
                    }
                }
            }
        }
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.DRILL, this.player.getMainHandStack()) > 0) {
            this.supplementary$LastBreakPos = pos;
            Direction d = Direction.getFacing(-this.player.getRotationVector().x, -this.player.getRotationVector()
                    .y, -this.player.getRotationVector().z);
            int x = d == Direction.EAST ? -1 : d == Direction.WEST ? 1 : 0;
            int y = d == Direction.UP ? -1 : d == Direction.DOWN ? 1 : 0;
            int z = d == Direction.SOUTH ? -1 : d == Direction.NORTH ? 1 : 0;
            BlockState baseState = this.world.getBlockState(pos);
            BlockPos p;
            int id = this.player.getId() + 1;
            BlockState bs = this.world.getBlockState(p = pos.add(x, y, z));
            if (EnchantHelper.isValidForBatchMining(this.player, this.world, pos, bs, baseState)) {
                this.supplementary$AdditionalBreakPos.add(new Pair<>(id++, p));
            }
            if (!bs.isAir()) {
                bs = this.world.getBlockState(p = p.add(x, y, z));
                if (EnchantHelper.isValidForBatchMining(this.player, this.world, pos, bs, baseState)) {
                    this.supplementary$AdditionalBreakPos.add(new Pair<>(id, p));
                }
            }
        }
        this.supplementary$AdditionalBreakPos.forEach(p -> EnchantHelper.setBlockBreakingInfoExclude(this.player.getId(),
                p.getLeft(), p.getRight(), this.blockBreakingProgress, this.world));
    }

}
