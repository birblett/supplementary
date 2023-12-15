package com.birblett.mixin.enchantments.oversized;

import com.birblett.registry.SupplementaryEnchantments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales 3rd person bow size up by 50% if enchanted with Oversized
 */
@Environment(EnvType.CLIENT)
@Mixin(HeldItemFeatureRenderer.class)
public class OversizedHeldItemFeatureRendererMixin {

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void scaleHeldBow(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack) > 0) {
            matrices.scale(1.5f, 1.5f, 1.5f);
            matrices.translate(0, 0, 0.15);
            if (entity.isUsingItem() && entity.getActiveItem() == stack) {
                matrices.translate(-0.07, 0.07, 0);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-5));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10));
            }
        }
    }
}
