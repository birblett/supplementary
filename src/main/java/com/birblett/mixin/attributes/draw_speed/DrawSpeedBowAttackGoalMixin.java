package com.birblett.mixin.attributes.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Pull progress compatibility for non-player entities
 */
@Mixin(BowAttackGoal.class)
public class DrawSpeedBowAttackGoalMixin {

    @Shadow @Final private HostileEntity actor;

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=20", ordinal = 2))
    private int modifyReleaseThreshold(int threshold) {
        ItemStack stack;
        if ((stack = actor.getActiveItem()) != null) {
            return (int) (threshold / EnchantHelper.customPullProgress(actor, threshold, stack));
        }
        return threshold;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/RangedAttackMob;attack(Lnet/minecraft/entity/LivingEntity;F)V"),
            index = 1)
    private float pullProgress(float pullProgress, @Local int i) {
        ItemStack stack;
        if ((stack = actor.getActiveItem()) != null) {
            return EnchantHelper.customPullProgress(actor, i, stack);
        }
        return pullProgress;
    }
}
