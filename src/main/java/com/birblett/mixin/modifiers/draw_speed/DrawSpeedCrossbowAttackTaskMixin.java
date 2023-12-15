package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Replace {@link net.minecraft.item.CrossbowItem#getPullTime(ItemStack)} with {@link EnchantHelper#customCrossbowPullTime(LivingEntity, ItemStack)}
 */
@Mixin(CrossbowAttackTask.class)
public class DrawSpeedCrossbowAttackTaskMixin {

    @ModifyExpressionValue(method = "tickState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private int replacePullTime(int pullTime, @Local MobEntity entity) {
        return EnchantHelper.customCrossbowPullTime(entity, entity.getActiveItem());
    }

}
