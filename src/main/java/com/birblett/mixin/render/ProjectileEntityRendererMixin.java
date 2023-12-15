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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Apply rendering effects from ProjectileEntity components to their renderer
 */
@Environment(EnvType.CLIENT)
@Mixin(ProjectileEntityRenderer.class)
public class ProjectileEntityRendererMixin {

    @Unique private int[] rgbl;

    @Inject(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void onRender(PersistentProjectileEntity persistentProjectileEntity, float f, float tickDelta, MatrixStack matrixStack,
                               VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        rgbl = new int[]{255, 255, 255, light};
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            int level = componentKey.get(persistentProjectileEntity).getValue();
            if (level > 0) {
                componentKey.get(persistentProjectileEntity).onProjectileRender((ProjectileEntityRenderer<PersistentProjectileEntity>) (Object) this,
                        persistentProjectileEntity, tickDelta, matrixStack, vertexConsumerProvider, rgbl, level);
            }
        }
    }

    @ModifyArgs(method = "vertex", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"))
    private void setRGB(Args args) {
        args.setAll(rgbl[0], rgbl[1], rgbl[2], 255);
    }

    @ModifyArg(method = "vertex", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;light(I)Lnet/minecraft/client/render/VertexConsumer;"))
    private int setLight(int light) {
        return rgbl[3];
    }

}
