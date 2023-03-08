package com.birblett.mixin.enchantments.slimed;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies increased slipperiness and jump height with Slimed enchantment equipped
 */
@Mixin(LivingEntity.class)
public abstract class SlimedLivingEntityMixin {

    @Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
    private void applyJumpBoost(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if ((EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0)) {
            float jumpBoost = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ACROBATIC, self) > 0 ? 2.0f : 1.5f;
            cir.setReturnValue(cir.getReturnValue() * jumpBoost);
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "travel", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/block/Block;getSlipperiness()F"), index = 7)
    private float applySlimedSlipperiness(float slipperiness) {
        LivingEntity self = (LivingEntity) (Object) this;
        slipperiness = (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) ? slipperiness + 0.4f : slipperiness;
        return slipperiness;
    }

    @Inject(method = "getMovementSpeed(F)F", at = @At("RETURN"), cancellable = true)
    private void amendSlipperinessSlowdown(float slipperiness, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if ((EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0)) {
            cir.setReturnValue(cir.getReturnValue() * slipperiness * slipperiness * slipperiness);
        }
    }

    @ModifyArg(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private ParticleEffect replaceSlimedFallParticles(ParticleEffect particleEffect) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState());
        }
        return particleEffect;
    }

    @ModifyVariable(method = "handleFallDamage", at = @At("HEAD"), index = 1, argsOnly = true)
    private float modifySlimedFallDistance(float fallDistance) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            int reduceFallDamage = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ACROBATIC, self) > 0 ? 4 : 3;
            return fallDistance / reduceFallDamage;
        }
        return fallDistance;
    }
}
