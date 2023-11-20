package com.birblett.lib.helper;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import static com.birblett.Supplementary.SUPPLEMENTARY_RANDOM;

/**
 * Helper functions for vector math.
 */
public class VectorHelper {

    private static final Vec3d X_AXIS = new Vec3d(1, 0, 0);
    private static final Vec3d Y_AXIS = new Vec3d(0, 1, 0);

    public static Vec3i fromVec3d(Vec3d v) {
        return new Vec3i((int) v.x, (int) v.y, (int) v.z);
    }

    public static Vec3i fromCoords(double x, double y, double z) {
        return new Vec3i((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    /**
     * Applies a random degree of divergence to an input vector. First rotates a random amount up to the input angle
     * about the x- or y-axes, then rotate a random amount about the input input vector
     * @param v Input vector
     * @param maxAngle Maximum random degree
     * @return Vector with divergence applied.
     */
    public static Vec3d applyRandomAngle(Vec3d v, double maxAngle) {
        double length = v.length();
        Vec3d vector = v.normalize();
        double angle = maxAngle / 360 * Math.PI * 2 * SUPPLEMENTARY_RANDOM.nextDouble();
        vector = rodriguesRotate(vector, (vector.y == 1 || vector.y == -1 ? X_AXIS : Y_AXIS).crossProduct(vector).normalize(), angle);
        angle = SUPPLEMENTARY_RANDOM.nextDouble() * Math.PI * 2;
        vector = rodriguesRotate(vector, v, angle).normalize().multiply(length);
        return vector;
    }

    /**
     * Apply angular (in radians) rotation to vector about an axis, using Rodrigues' rotation formula: <code>rotated =
     * v * cos(angle) + (v x axis) * sin(angle) + axis * (v • axis) * 1 - cos(angle)</code>
     * @param v Input vector
     * @param axis Axis to rotate aobut
     * @param angle Angle to rotate
     * @return The rotated vector
     */
    public static Vec3d rodriguesRotate(Vec3d v, Vec3d axis, double angle) {
        Vec3d scale = v.multiply(Math.cos(angle));
        Vec3d skew = v.crossProduct(axis).multiply(Math.sin(angle));
        Vec3d offset = axis.multiply(v.dotProduct(axis)).multiply(1 - Math.cos(angle));
        return scale.add(skew).add(offset);
    }
}
