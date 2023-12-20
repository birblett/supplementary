package com.birblett.lib.api;

import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * See {@link me.x150.renderer.render.Renderer3d.FadingBlock}.
 */
public record StaticFadingBlock(Color outline, Color fill, Vec3d start, Vec3d dimensions, long created, long lifeTime) {

    public static final List<StaticFadingBlock> STATIC_FADES = new CopyOnWriteArrayList<>();

    public static void addStaticFadingBlock(Color outlineColor, Color fillColor, Vec3d start, Vec3d dimensions, long lifeTimeMs) {
        StaticFadingBlock fb = new StaticFadingBlock(outlineColor, fillColor, start, dimensions, System.currentTimeMillis(),
                lifeTimeMs);

        STATIC_FADES.removeIf(fadingBlock -> fadingBlock.start.equals(start) && fadingBlock.dimensions.equals(dimensions));
        STATIC_FADES.add(fb);
    }

    public long getLifeTimeLeft() {
        return Math.max(0, created - System.currentTimeMillis() + lifeTime);
    }

    public boolean isDead() {
        return getLifeTimeLeft() == 0;
    }

}
