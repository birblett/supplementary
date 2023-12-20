package com.birblett.mixin.attributes.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Replace {@link net.minecraft.item.CrossbowItem#getPullTime(ItemStack)} with {@link EnchantHelper#customCrossbowPullTime(LivingEntity, ItemStack)}
 */
@Mixin(CrossbowAttackGoal.class)
public class DrawSpeedCrossbowAttackGoalMixin {

    @Shadow @Final private HostileEntity actor;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private int replacePullTime(int pullTime) {
        return EnchantHelper.customCrossbowPullTime(actor, actor.getActiveItem());
    }

}
