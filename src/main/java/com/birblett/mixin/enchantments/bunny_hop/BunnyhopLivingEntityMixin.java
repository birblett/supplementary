package com.birblett.mixin.enchantments.bunny_hop;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public class BunnyhopLivingEntityMixin {
    /*
    Applies various movement buffs/debuffs associated with the bunnyhop enchnat
     */

    @ModifyArgs(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    private void boostJumpSpeed(Args args) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BUNNYHOP, self) > 0) {
            args.set(0, (double) args.get(0) * 2.7);
            args.set(2, (double) args.get(2) * 2.7);
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
            self.setVelocity(self.getVelocity().multiply(0.85, 1, 0.85));
        }
    }
}
