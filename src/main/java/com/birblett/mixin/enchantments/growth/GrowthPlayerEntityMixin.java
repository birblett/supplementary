package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies attack and mining speed bonuses from the Growth enchantment
 */
@Mixin(PlayerEntity.class)
public class GrowthPlayerEntityMixin {

    @Inject(method = "getAttackCooldownProgressPerTick", at = @At("RETURN"), cancellable = true)
    private void modifyAttackSpeed(CallbackInfoReturnable<Float> cir) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, ((PlayerEntity) (Object) this).getMainHandStack()) > 0) {
            cir.setReturnValue(cir.getReturnValue() / ((EnchantHelper.getGrowthStat(((PlayerEntity) (Object) this).getMainHandStack(),
                    EnchantHelper.GrowthKey.ATTACK_SPEED)) + 1));
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void addBlockBreakSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity user = ((PlayerEntity) (Object) this);
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, user.getMainHandStack()) > 0) {
            cir.setReturnValue(cir.getReturnValue() * (1 + EnchantHelper.getGrowthStat(user.getMainHandStack(), (user.getInventory().getBlockBreakingSpeed(block) > 1 ?
                    EnchantHelper.GrowthKey.MINING_SPEED : EnchantHelper.GrowthKey.ALT_MINING_SPEED))));
        }
    }
}
