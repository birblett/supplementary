package com.birblett.mixin.enchantments.oversized;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class OversizedHeldItemRendererMixin {

    @Unique private ItemStack supplementary$BowItemStack;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I",
            ordinal = 1))
    private void getOversizedLevel(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        this.supplementary$BowItemStack = item;
    }

    @ModifyVariable(method = "renderFirstPersonItem", at = @At(value = "STORE", ordinal = 2), index = 16)
    private float scaleModelPullProgress(float pullProgress){
        return SupplementaryEnchantmentHelper.getDrawspeedModifier(pullProgress, supplementary$BowItemStack);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V",
            ordinal = 8))
    private void scaleFirstPersonUsingBow(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, item) > 0) {
            matrices.translate(0, -0.1, 0);
            matrices.scale(1.3f, 1.3f, 1.3f);
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
            ordinal = 12))
    private void scaleFirstPersonIdleBow(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof BowItem && EnchantmentHelper.getLevel(SupplementaryEnchantments.OVERSIZED, item) > 0) {
            matrices.translate(-0.2, 0.1, 0.17);
        }
    }
}
