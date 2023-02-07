package com.birblett.lib.helper;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class RenderHelper {

    public static void ropeRender(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, Entity rootEntity) {
        matrices.push();
        Vec3d vec3d = rootEntity.getLeashPos(tickDelta);
        double g = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double h = MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double i = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        float j = (float)(vec3d.x - g);
        float k = (float)(vec3d.y - h);
        float l = (float)(vec3d.z - i);

        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = new BlockPos(entity.getCameraPosVec(tickDelta));
        BlockPos blockPos2 = new BlockPos(rootEntity.getCameraPosVec(tickDelta));
        int q = entity.isOnFire() ? 15 : entity.world.getLightLevel(LightType.BLOCK, blockPos);
        int r = rootEntity.isOnFire() ? 15 : rootEntity.world.getLightLevel(LightType.BLOCK, blockPos);
        int s = entity.world.getLightLevel(LightType.SKY, blockPos);
        int t = entity.world.getLightLevel(LightType.SKY, blockPos2);

        int u;
        for(u = 0; u <= 24; ++u) {
            renderRopePiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, o, p, u);
        }

        for(u = 24; u >= 0; --u) {
            renderRopePiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.0F, o, p, u);
        }
        matrices.pop();
    }

    private static void renderRopePiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int projectileBlockLight, int ownerBlockLight, int projectileSkyLight, int ownerSkyLight, float j, float k, float l, int pieceIndex) {
        float index = (float)pieceIndex / 24.0F;
        int blockLight = (int)MathHelper.lerp(index, (float)projectileBlockLight, (float)ownerBlockLight);
        int skyLight = (int)MathHelper.lerp(index, (float)projectileSkyLight, (float)ownerSkyLight);
        int lightmap = LightmapTextureManager.pack(blockLight, skyLight);
        float u = f * index;
        float v = g > 0.0F ? g * index * index : g - g * (1.0F - index) * (1.0F - index);
        float w = h * index;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(0.1F, 0.1F, 0.1F, 1.0F).light(lightmap).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + (float) 0.025 - j, w - l).color(0.1F, 0.1F, 0.1F, 1.0F).light(lightmap).next();
    }

}
