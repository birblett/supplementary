package com.birblett.mixin.events;

import com.birblett.Supplementary;
import com.birblett.lib.api.EntityEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Allows for block break events to be called from the client, but necessitates client/server handling in onBlockBreak events.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique private Direction supplementary$breakBlockFace;

    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void getFaceAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        this.supplementary$breakBlockFace = direction;
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void getFaceUpdateProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        this.supplementary$breakBlockFace = direction;
    }

    @Inject(method = "breakBlock", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onSuccessfulMineEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState blockState, Block block) {
        if (this.client.player != null) {
            EntityEvents.POSTMINE_EVENT.invoker().onBlockBreakEvent(world, blockState, pos, this.client.player, this.client.player
                    .getMainHandStack(), true, this.supplementary$breakBlockFace);
        }
    }
}
