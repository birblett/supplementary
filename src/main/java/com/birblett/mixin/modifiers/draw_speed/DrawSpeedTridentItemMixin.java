package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Applies draw speed modifiers to tridents
 */
@Mixin(TridentItem.class)
public class DrawSpeedTridentItemMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "onStoppedUsing", at = @At(value = "STORE", ordinal = 0), index = 6)
    private int setUseTimeRemaining(int i, @Local LivingEntity user, @Local ItemStack stack) {
        if (user.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            i *= user.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 10.0f;
        }
        return i;
    }

}
