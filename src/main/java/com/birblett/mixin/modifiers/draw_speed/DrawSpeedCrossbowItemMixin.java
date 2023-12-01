package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Crossbow draw speed scaling. Uses dumb hack because mixins are limited, but avoids a redirect for compatibility.
 */
@Mixin(CrossbowItem.class)
public class DrawSpeedCrossbowItemMixin {

    @ModifyExpressionValue(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullProgress(ILnet/minecraft/item/ItemStack;)F"))
    private float overWritePullProgress(float pullProgress, @Local LivingEntity user, @Local ItemStack stack, @Local(ordinal = 1) int i) {
        return EnchantHelper.getDrawSpeedModifier(user, (float) i / (float)CrossbowItem.getPullTime(stack),
                stack);
    }

    @ModifyExpressionValue(method = "usageTick", at = @At(value = "CONSTANT", args = "floatValue=0.2f", ordinal = 0))
    private float modifyNoChargeCond(float f, @Local LivingEntity user, @Local ItemStack stack) {
        return f / EnchantHelper.getDrawSpeedModifier(user, 1.0f, stack);
    }

    @ModifyExpressionValue(method = "usageTick", at = @At(value = "CONSTANT", args = "floatValue=0.2f", ordinal = 1))
    private float scaleChargeSoundCond(float f, @Local LivingEntity user, @Local ItemStack stack) {
        return f / EnchantHelper.getDrawSpeedModifier(user, 1.0f, stack);
    }

    @ModifyExpressionValue(method = "usageTick", at = @At(value = "CONSTANT", args = "floatValue=0.5f"))
    private float scaleLoadSoundCond(float f, @Local LivingEntity user, @Local ItemStack stack) {
        return f / EnchantHelper.getDrawSpeedModifier(user, 1.0f, stack);
    }

}
