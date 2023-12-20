package com.birblett.mixin.attributes.mining_speed;

import com.birblett.registry.SupplementaryAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addMiningSpeedAttribute(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(SupplementaryAttributes.MINING_SPEED).add(SupplementaryAttributes.EFFECTIVE_MINING_SPEED)
                .add(SupplementaryAttributes.INEFFECTIVE_MINING_SPEED);
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void applyBlockBreakingSpeedMods(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity p = (PlayerEntity) (Object) this;
        float mult = 1.0f;
        if (p.getAttributeInstance(SupplementaryAttributes.MINING_SPEED) != null) {
            mult *= (float) (p.getAttributeValue(SupplementaryAttributes.MINING_SPEED) / 10.0);
        }
        if (p.getAttributeInstance(SupplementaryAttributes.EFFECTIVE_MINING_SPEED) != null && p.getInventory().getBlockBreakingSpeed(
                block) > 1) {
            mult *= p.getAttributeValue(SupplementaryAttributes.EFFECTIVE_MINING_SPEED) / 10.0;
        }
        if (p.getAttributeInstance(SupplementaryAttributes.INEFFECTIVE_MINING_SPEED) != null && p.getInventory().getBlockBreakingSpeed(
                block) <= 1) {
            mult *= p.getAttributeValue(SupplementaryAttributes.INEFFECTIVE_MINING_SPEED) / 10.0;
        }
        cir.setReturnValue(cir.getReturnValue() * mult);
    }

}
