package com.birblett.client.render;

import com.birblett.Supplementary;
import com.birblett.entities.BoomerangEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {

    private final ItemRenderer itemRenderer;

    public BoomerangEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public Identifier getTexture(BoomerangEntity entity) {
        return null;
    }

    @Override
    public void render(BoomerangEntity boomerangEntity, float wtf, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        /*
        heavily simplified and modified item renderer
         */
        if (!boomerangEntity.isRemoved()) {
            matrixStack.push();
            // rotate based on yaw - this is only set once on initial use
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(tickDelta, boomerangEntity.prevYaw, boomerangEntity.getYaw()) - 90.0f));
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(tickDelta, boomerangEntity.prevPitch, boomerangEntity.getPitch())));
            ItemStack itemStack = boomerangEntity.getStack();
            BakedModel bakedModel = this.itemRenderer.getModel(itemStack, boomerangEntity.world, null, boomerangEntity.getId());
            matrixStack.scale(1.2f, 1.2f, 1.2f);
            // center on hitbox, and rotate based on age
            matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(MathHelper.lerp(tickDelta, boomerangEntity.getAge(), boomerangEntity.getAge() + 1)));
            matrixStack.translate(0.03f, 0.05f, -0.15f);
            // rotate to be flat instead of upright
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            matrixStack.push();
            this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrixStack.pop();
            matrixStack.pop();
            super.render(boomerangEntity, wtf, tickDelta, matrixStack, vertexConsumerProvider, light);
        }
    }
}
