package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Render crossbows properly with the draw speed attribute.
 */
@Mixin(HeldItemRenderer.class)
public class DrawSpeedHeldItemRendererMixin {

    @ModifyExpressionValue(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getItemUseTimeLeft()I"))
    private int modifyMaxUseTime(int useTime, @Local AbstractClientPlayerEntity player) {
        if (player.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            return (int) (player.getActiveItem().getMaxUseTime() / player.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) *
                    10.0f) - player.getItemUseTime();
        }
        return useTime;
    }

    @ModifyExpressionValue(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private int replacePullTime(int pullTime, @Local AbstractClientPlayerEntity entity, @Local ItemStack stack) {
        return EnchantHelper.customCrossbowPullTime(entity, stack);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "renderFirstPersonItem", at = @At(value = "STORE", ordinal = 5), index = 16)
    private float scaleModelPullProgress(float pullProgress, @Local AbstractClientPlayerEntity player, @Local(ordinal = 0) ItemStack item){
        if (player.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            pullProgress *= player.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 10.0f;
        }
        return pullProgress;
    }

}
