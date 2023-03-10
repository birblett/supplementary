package com.birblett.mixin.enchantments.bunny_hop;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Applies 2.8x horizontal jump velocity, 0.75x jump vertical velocity, and 0.85x overall movement speed penalty with
 * Bunny Hop equipped
 */
@Mixin(LivingEntity.class)
public class BunnyHopLivingEntityMixin {

    @ModifyArgs(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    private void boostJumpSpeed(Args args) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BUNNYHOP, self) > 0) {
            double boost = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ALL_TERRAIN, self) > 0 ? 3.1 : 2.8;
            args.set(0, (double) args.get(0) * boost);
            args.set(2, (double) args.get(2) * boost);
        }
    }

    @Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
    private void decreaseJumpHeight(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BUNNYHOP, self) > 0) {
            cir.setReturnValue(cir.getReturnValue() * 0.75f);
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void decreaseMovementSpeed(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BUNNYHOP, self) > 0 && self.isOnGround()) {
            double movementPenalty = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ALL_TERRAIN, self) > 0 ? 0.9 : 0.85;
            self.setVelocity(self.getVelocity().multiply(movementPenalty, 1, movementPenalty));
        }
    }
}
