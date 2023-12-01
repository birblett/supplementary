package com.birblett.mixin.modifiers.draw_speed;

import com.birblett.lib.helper.EnchantHelper;
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
        cir.setReturnValue(EnchantHelper.getDrawSpeedModifier(entity, cir.getReturnValue(), stack));
    }

    @Inject(method = "method_27888(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;I)F",
            at = @At("RETURN"), cancellable = true)
    private static void applyCrossbowDrawSpeedModifiers(ItemStack stack, ClientWorld world, LivingEntity entity, int seed, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EnchantHelper.getDrawSpeedModifier(entity, cir.getReturnValue(), stack));
    }
}
