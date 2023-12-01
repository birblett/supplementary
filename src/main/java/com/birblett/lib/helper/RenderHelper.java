package com.birblett.lib.helper;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

/**
 * Helper functions for rendering certain components.
 */
public class RenderHelper {

    /**
     * Used to render a grappling line between the user and the fired arrow. Mostly copied from {@link MobEntityRenderer#renderLeash(
     * net.minecraft.entity.mob.MobEntity, float, net.minecraft.client.util.math.MatrixStack,
     * net.minecraft.client.render.VertexConsumerProvider, net.minecraft.entity.Entity)}
     */
    public static void ropeRender(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, Entity rootEntity, Hand hand) {
        matrices.push();
        Vec3d vec3d = rootEntity.getLerpedPos(tickDelta).add(0.0, rootEntity.getStandingEyeHeight() * 0.7, 0.0);
        double g = net.minecraft.util.math.MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double h = net.minecraft.util.math.MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double i = net.minecraft.util.math.MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        float j = (float)(vec3d.x - g);
        float k = (float)(vec3d.y - h);
        float l = (float)(vec3d.z - i);

        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = net.minecraft.util.math.MathHelper.inverseSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = new BlockPos(GenMathHelper.fromVec3d(entity.getCameraPosVec(tickDelta)));
        BlockPos blockPos2 = new BlockPos(GenMathHelper.fromVec3d(rootEntity.getCameraPosVec(tickDelta)));
        int q = entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, blockPos);
        int r = rootEntity.isOnFire() ? 15 : rootEntity.getWorld().getLightLevel(LightType.BLOCK, blockPos);
        int s = entity.getWorld().getLightLevel(LightType.SKY, blockPos);
        int t = entity.getWorld().getLightLevel(LightType.SKY, blockPos2);

        int u;
        for(u = 0; u <= 24; ++u) {
            renderRopePiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, o, p, u);
        }

        for(u = 24; u >= 0; --u) {
            renderRopePiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.0F, o, p, u);
        }
        matrices.pop();
    }

    /**
     * Renders a single segment of grappling rope. Mostly copied from {@link MobEntityRenderer#renderLeashPiece(VertexConsumer, Matrix4f, float, float, float, int, int, int, int, float, float, float, float, int, boolean)}
     */
    private static void renderRopePiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int projectileBlockLight, int ownerBlockLight, int projectileSkyLight, int ownerSkyLight, float j, float k, float l, int pieceIndex) {
        float index = (float)pieceIndex / 24.0F;
        int blockLight = (int) net.minecraft.util.math.MathHelper.lerp(index, (float)projectileBlockLight, (float)ownerBlockLight);
        int skyLight = (int) net.minecraft.util.math.MathHelper.lerp(index, (float)projectileSkyLight, (float)ownerSkyLight);
        int lightmap = LightmapTextureManager.pack(blockLight, skyLight);
        float u = f * index;
        float v = g > 0.0F ? g * index * index : g - g * (1.0F - index) * (1.0F - index);
        float w = h * index;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(0.1F, 0.1F, 0.1F, 1.0F).light(lightmap).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + (float) 0.025 - j, w - l).color(0.1F, 0.1F, 0.1F, 1.0F).light(lightmap).next();
    }

}
