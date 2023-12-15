package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Replace {@link net.minecraft.item.CrossbowItem#getPullTime(ItemStack)} with {@link EnchantHelper#customCrossbowPullTime(LivingEntity, ItemStack)}
 */
@Mixin(CrossbowPosing.class)
public class DrawSpeedCrossbowPosingMixin {

    @ModifyExpressionValue(method = "charge", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private static int replacePullTime(int pullTime, @Local LivingEntity entity) {
        return EnchantHelper.customCrossbowPullTime(entity, entity.getActiveItem());
    }

}
