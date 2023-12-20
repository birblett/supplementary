package com.birblett.mixin.enchantments.mining_enchants;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import com.birblett.registry.SupplementaryPacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Pair;
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
 * Block break outlines on the client for Drill and Excavation enchants. Also sends direction update packets.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MiningEnchClientPlayerInteractionMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private boolean breakingBlock;
    @Shadow public abstract int getBlockBreakingProgress();
    @Unique private Direction supplementary$LastBreakingDirection = null;
    @Unique private BlockPos supplementary$LastBreakPos = null;
    @Unique private final List<Pair<Integer, BlockPos>> supplementary$AdditionalBreakPos = new ArrayList<>();

    @Inject(method = "cancelBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private void stopBreakReset(CallbackInfo ci) {
        if (this.client.world != null) {
            this.supplementary$AdditionalBreakPos.forEach((pair -> this.client.world.setBlockBreakingInfo(pair.getLeft(), pair.getRight(),
                    -1)));
            this.supplementary$AdditionalBreakPos.clear();
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void resetBlockBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.world != null && this.client.player != null && ((this.supplementary$LastBreakingDirection != null
                && direction != this.supplementary$LastBreakingDirection) || pos != this.supplementary$LastBreakPos)) {
            this.supplementary$AdditionalBreakPos.forEach((pair -> this.client.world.setBlockBreakingInfo(pair.getLeft(), pair.getRight(),
                    -1)));
            this.supplementary$AdditionalBreakPos.clear();
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setBlockBreakingInfo(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void addAllBlockBreaks(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.world != null && this.client.player != null && this.breakingBlock) {
            this.supplementary$LastBreakPos = pos;
            if (EnchantmentHelper.getLevel(SupplementaryEnchantments.EXCAVATION, this.client.player.getMainHandStack()) > 0) {
                this.supplementary$LastBreakingDirection = direction;
                int x = (direction != Direction.EAST && direction != Direction.WEST) ? 1 : 0;
                int y = (direction != Direction.UP && direction != Direction.DOWN) ? 1 : 0;
                int z = (direction != Direction.NORTH && direction != Direction.SOUTH) ? 1 : 0;
                BlockState baseState = this.client.world.getBlockState(pos);
                int id = this.client.player.getId() + 1;
                this.supplementary$AdditionalBreakPos.clear();
                for (int i = -x; i < x + 1; i++) {
                    for (int j = -y; j < y + 1; j++) {
                        for (int k = -z; k < z + 1; k++) {
                            BlockPos p = pos.add(i, j, k);
                            BlockState bs = this.client.world.getBlockState(p);
                            if (EnchantHelper.isValidForBatchMining(this.client.player, this.client.world, pos, bs, baseState)) {
                                this.supplementary$AdditionalBreakPos.add(new Pair<>(id++, p));
                            }
                        }
                    }
                }
                (new SupplementaryPacketRegistry.MiningDirectionC2SPacket(direction)).sendC2S();
            }
            if (EnchantmentHelper.getLevel(SupplementaryEnchantments.DRILL, this.client.player.getMainHandStack()) > 0) {
                this.supplementary$LastBreakingDirection = null;
                Direction d = Direction.getFacing(-this.client.player.getRotationVector().x, -this.client.player.getRotationVector()
                        .y, -this.client.player.getRotationVector().z);
                int x = d == Direction.EAST ? -1 : d == Direction.WEST ? 1 : 0;
                int y = d == Direction.UP ? -1 : d == Direction.DOWN ? 1 : 0;
                int z = d == Direction.SOUTH ? -1 : d == Direction.NORTH ? 1 : 0;
                BlockState baseState = this.client.world.getBlockState(pos);
                BlockPos p;
                int id = this.client.player.getId() + 1;
                BlockState bs = this.client.world.getBlockState(p = pos.add(x, y, z));
                if (EnchantHelper.isValidForBatchMining(this.client.player, this.client.world, pos, bs, baseState)) {
                    this.supplementary$AdditionalBreakPos.add(new Pair<>(id++, p));
                }
                if (!bs.isAir()) {
                    bs = this.client.world.getBlockState(p = p.add(x, y, z));
                    if (EnchantHelper.isValidForBatchMining(this.client.player, this.client.world, pos, bs, baseState)) {
                        this.supplementary$AdditionalBreakPos.add(new Pair<>(id, p));
                    }
                }
            }
            this.supplementary$AdditionalBreakPos.forEach(p -> this.client.world.setBlockBreakingInfo(p.getLeft(), p.getRight(), this.getBlockBreakingProgress()));
        }
    }

}
