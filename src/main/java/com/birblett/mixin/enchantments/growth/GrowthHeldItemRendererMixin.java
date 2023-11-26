package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Increase charge rate of tridents with Growth in the first-person view
 */
@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class GrowthHeldItemRendererMixin {

    @Unique private ItemStack supplementary$TridentItemStack;
    @Unique private LivingEntity supplementary$Holder;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I",
            ordinal = 2))
    private void getItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        this.supplementary$TridentItemStack = item;
        this.supplementary$Holder = player;
    }

    @ModifyVariable(method = "renderFirstPersonItem", at = @At(value = "STORE", ordinal = 5), index = 16)
    private float scaleModelPullProgress(float pullProgress){
        return SupplementaryEnchantmentHelper.getDrawspeedModifier(this.supplementary$Holder, pullProgress, this.supplementary$TridentItemStack);
    }

}
