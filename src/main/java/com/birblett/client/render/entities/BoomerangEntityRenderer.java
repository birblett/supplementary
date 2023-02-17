package com.birblett.client.render.entities;

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
    /*
    Renderer component for boomerang entities. Do not call manually!
     */

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
            // rotate based on yaw and pitch
            if (boomerangEntity.getYaw() == 0) {
                // handle facing straight up or down
                int mult = boomerangEntity.getPitch() >= 0 ? 1 : -1;
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(boomerangEntity.getPitch()));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mult * (boomerangEntity.getStoredAngle() - 90)));
            }
            else {
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(boomerangEntity.getYaw() - 90.0f));
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(boomerangEntity.getPitch()));
            }
            // get item model from boomerang, scaled to fit hitbox
            ItemStack itemStack = boomerangEntity.getStack();
            BakedModel bakedModel = this.itemRenderer.getModel(itemStack, boomerangEntity.world, null, boomerangEntity.getId());
            matrixStack.scale(1.2f, 1.2f, 1.2f);
            // handle spin animation
            matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(MathHelper.lerp(tickDelta, boomerangEntity.getAge(), boomerangEntity.getAge() + 1)));
            // center model on hitbox
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
