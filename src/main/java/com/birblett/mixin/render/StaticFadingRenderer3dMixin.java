package com.birblett.mixin.render;

import com.birblett.lib.api.StaticFadingBlock;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static me.x150.renderer.render.Renderer3d.modifyColor;
import static me.x150.renderer.render.Renderer3d.renderEdged;

/**
 * Allows non-shrinking fading blocks to be rendered.
 */
@Mixin(Renderer3d.class)
public class StaticFadingRenderer3dMixin {

    @Inject(method = "renderFadingBlocks", at = @At("HEAD"))
    private static void renderStatic(MatrixStack stack, CallbackInfo ci) {
        Renderer3d.renderThroughWalls();
        StaticFadingBlock.STATIC_FADES.removeIf(StaticFadingBlock::isDead);
        for (StaticFadingBlock block : StaticFadingBlock.STATIC_FADES) {
            if (block == null) {
                continue;
            }
            long lifetimeLeft = block.getLifeTimeLeft();
            double progress = lifetimeLeft / (double) block.lifeTime();
            progress = MathHelper.clamp(progress, 0, 1);
            Color out = modifyColor(block.outline(), -1, -1, -1, (int) (block.outline()
                    .getAlpha() * progress));
            Color fill = modifyColor(block.fill(), -1, -1, -1, (int) (block.fill().getAlpha()
                    * progress));
            renderEdged(stack, fill, out, block.start(), block.dimensions());
        }
        Renderer3d.stopRenderThroughWalls();
    }

}
