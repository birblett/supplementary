package com.birblett.mixin.functional.render;

import com.birblett.registry.SupplementaryEnchantments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemFeatureRenderer.class)
public class OversizedHeldItemFeatureRendererMixin {

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void scaleHeldBow(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof BowItem && EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, stack) > 0) {
            matrices.scale(1.5f, 1.5f, 1.5f);
            matrices.translate(0, 0, 0.15);
            if (entity.isUsingItem() && entity.getActiveItem() == stack) {
                matrices.translate(-0.07, 0.07, 0);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-5));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-10));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-10));
            }
        }
    }
}
