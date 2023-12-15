package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Scale pull progress based on current draw speed modifier
 */
@Mixin(BowItem.class)
public class DrawSpeedBowItemMixin {

    @ModifyExpressionValue(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float useCustomPullProgress(float f, @Local LivingEntity holder, @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 0)
            int remainingUseTicks) {
        if (holder.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            return EnchantHelper.customPullProgress(holder, ((BowItem) (Object) this).getMaxUseTime(stack) - remainingUseTicks, stack);
        }
        return f;
    }


}
