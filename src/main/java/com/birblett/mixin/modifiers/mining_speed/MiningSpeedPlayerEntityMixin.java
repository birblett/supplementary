package com.birblett.mixin.modifiers.mining_speed;

import com.birblett.lib.helper.EnchantHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies block breaking speed modifiers.
 */
@Mixin(PlayerEntity.class)
public class MiningSpeedPlayerEntityMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void applyBlockBreakingSpeedMods(BlockState block, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() * EnchantHelper.getBlockBreakModifier((PlayerEntity) (Object) this,
                block));
    }
}
