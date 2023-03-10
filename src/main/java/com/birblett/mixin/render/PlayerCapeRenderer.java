package com.birblett.mixin.render;

import com.birblett.client.render.items.CapeFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the custom cape renderer to the player model
 */
@Environment(value= EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerCapeRenderer extends LivingEntityRenderer {

    @SuppressWarnings("unchecked")
    public PlayerCapeRenderer(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;<init>(Lnet/minecraft/client/render/" +
                     "entity/EntityRendererFactory$Context;Z)V", at = @At("RETURN"))
    public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.addFeature(new CapeFeatureRenderer(this));
    }
}