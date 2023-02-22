package com.birblett.mixin.functional;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class BoostingEntityMixin {
    /*
    Adjusts step height for entities during internal calculations if Boosting enchant present
     */

    @Unique private boolean resetStepHeight;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void adjustStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        this.resetStepHeight = (Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BOOSTING, self) > 0;
        if (this.resetStepHeight) {
            ((Entity) (Object) this).stepHeight += 0.6;
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void returnCalcStepHeightAdjustment(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.resetStepHeight) {
            ((Entity) (Object) this).stepHeight -= 0.6;
            this.resetStepHeight = false;
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"))
    private void tailCalcStepHeightAdjustment(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.resetStepHeight) {
            ((Entity) (Object) this).stepHeight -= 0.6;
        }
    }
}
