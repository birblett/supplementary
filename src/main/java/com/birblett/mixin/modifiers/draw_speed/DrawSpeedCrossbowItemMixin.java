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
 * Replace Crossbow static method calls for pull progress/time with custom method calls.
 */
@Mixin(CrossbowItem.class)
public class DrawSpeedCrossbowItemMixin {

    @ModifyExpressionValue(method = "getMaxUseTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private static int modifyPullTime(int f, @Local ItemStack stack) {
        return EnchantHelper.customCrossbowPullTime(null, stack);
    }

    @ModifyExpressionValue(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullProgress(ILnet/minecraft/item/ItemStack;)F"))
    private static float modifyPullProgress(float f, @Local(ordinal = 1) int useTime, @Local LivingEntity user, @Local ItemStack stack) {
        return EnchantHelper.customCrossbowPullProgress(useTime, user, stack);
    }

}
