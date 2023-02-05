package com.birblett.trinkets;


import com.birblett.registry.SupplementaryItems;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Optional;

public class CustomCapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final ModelPart BASE_CAPE;

    public CustomCapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity,
                                     PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cape", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F,
                        0.0F, 0.0F, 20.0F, 40.0F, 1.0F), ModelTransform.NONE);
        BASE_CAPE = TexturedModelData.of(modelData, 64, 64).createModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);
        if (trinketComponent.isPresent() && trinketComponent.get().isEquipped(SupplementaryItems.CAPE) && player.getCapeTexture() == null) {
            List<Pair<SlotReference, ItemStack>> itemList = trinketComponent.get().getEquipped(SupplementaryItems.CAPE);
            if (player.isInvisible() || player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) || itemList.size() < 1) {
                return;
            }
            ItemStack stack = itemList.get(0).getRight();
            matrices.push();
            // vanilla-ish cape orientation calc, z-offset is slightly altered
            boolean hasBodyGear = !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
            matrices.translate(0.0, hasBodyGear ? -0.07 : 0.0, hasBodyGear ? 0.2375 : 0.175);
            double d = MathHelper.lerp(tickDelta, player.prevCapeX, player.capeX) - MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double e = MathHelper.lerp(tickDelta, player.prevCapeY, player.capeY) - MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double m = MathHelper.lerp(tickDelta, player.prevCapeZ, player.capeZ) - MathHelper.lerp(tickDelta, player.prevZ, player.getZ());
            float n = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);
            double o = MathHelper.sin(n * ((float)Math.PI / 180));
            double p = -MathHelper.cos(n * ((float)Math.PI / 180));
            float q = (float)e * 10.0f;
            q = MathHelper.clamp(q, -6.0f, 32.0f);
            float r = (float)(d * o + m * p) * 100.0f;
            r = MathHelper.clamp(r, 0.0f, 150.0f);
            float s = (float)(d * p - m * o) * 100.0f;
            s = MathHelper.clamp(s, -20.0f, 20.0f);
            if (r < 0.0f) {
                r = 0.0f;
            }
            float t = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
            q += MathHelper.sin(MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0f) * 32.0f * t;
            if (player.isInSneakingPose()) {
                q += 25.0f;
                // hacky magic numbers to fix cape orientation while crouching
                matrices.translate(0.0, hasBodyGear ? 0.101 : 0.118, hasBodyGear ? -0.065 : -0.02);
            }
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(6.0f + r / 2.0f + q));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0f));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - s / 2.0f));
            matrices.scale(0.5f, 0.5f, 1.0f);

            // render blank banner model with pattern overlay
            VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
            BASE_CAPE.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            List<com.mojang.datafixers.util.Pair<BannerPattern, DyeColor>> list =
                    BannerBlockEntity.getPatternsFromNbt(CapeItem.getBaseColor(stack), BannerBlockEntity.getPatternListNbt(stack));
            BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, BASE_CAPE,
                                                   ModelLoader.BANNER_BASE, true, list, false);
            matrices.pop();
        }
    }
}
