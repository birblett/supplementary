package com.birblett.mixin.attributes.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into the anonymous texture provider method for bows and crossbows; applies draw speed multipliers to pull progress
 */
@Environment(EnvType.CLIENT)
@Mixin(ModelPredicateProviderRegistry.class)
public class DrawSpeedModelProviderRegistryMixin {

    @Inject(method = "method_27890(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;I)F",
            at = @At("RETURN"), cancellable = true)
    private static void applyBowDrawSpeedModifiers(ItemStack stack, ClientWorld world, LivingEntity entity, int seed, CallbackInfoReturnable<Float> cir) {
        if (entity != null && entity.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            cir.setReturnValue((float) (cir.getReturnValue() * entity.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 10.0f));
        }
    }

    @ModifyExpressionValue(method = "method_27888(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;I)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I"))
    private static int replacePullTime(int pullTime, @Local LivingEntity entity, @Local ItemStack stack) {
        return EnchantHelper.customCrossbowPullTime(entity, stack);
    }

}
