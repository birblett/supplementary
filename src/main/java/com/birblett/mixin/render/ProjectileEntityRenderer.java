package com.birblett.mixin.render;

import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ProjectileEntityRenderer.class)
public class ProjectileEntityRenderMixin {

    @Inject(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void onRenderEvent(PersistentProjectileEntity persistentProjectileEntity, float f, float tickDelta, MatrixStack matrixStack,
                               VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            int level = componentKey.get(persistentProjectileEntity).getValue();
            if (level > 0) {
                componentKey.get(persistentProjectileEntity).onProjectileRender(persistentProjectileEntity, tickDelta,
                        matrixStack, vertexConsumerProvider, level);
            }
        }
    }
}
