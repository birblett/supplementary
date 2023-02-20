package com.birblett.lib.helper;

import net.minecraft.util.math.Vec3d;

import static com.birblett.Supplementary.SUPPLEMENTARY_RANDOM;

public class VectorHelper {

    private static final Vec3d X_AXIS = new Vec3d(1, 0, 0);
    private static final Vec3d Y_AXIS = new Vec3d(0, 1, 0);

    public static Vec3d applyRandomAngle(Vec3d inVector, double maxAngle) {
        /*
        takes a maximum divergence, in degrees, and applies it to the input vector
        relatively computationally expensive, usage in a constantly ticked context is not recommended
         */
        double length = inVector.length();
        Vec3d vector = inVector.normalize();
        // rotate about any axis with some random degree within the bound; the actual axis doesn't matter
        double angle = maxAngle / 360 * Math.PI * 2 * SUPPLEMENTARY_RANDOM.nextDouble();
        vector = rodriguesRotate(vector, (vector.y == 1 || vector.y == -1 ? X_AXIS : Y_AXIS).crossProduct(vector).normalize(), angle);
        // rotate 0-359 degrees about the input vector
        angle = SUPPLEMENTARY_RANDOM.nextDouble() * Math.PI * 2;
        vector = rodriguesRotate(vector, inVector, angle).normalize().multiply(length);
        return vector;
    }

    public static Vec3d rodriguesRotate(Vec3d v, Vec3d axis, double angle) {
        /*
        apply angular (in radians) rotation to vector about an axis, using rodrigues' rotation formula:
        rotated = v * cos(angle) + (v x axis) * sin(angle) + axis * (v • axis) * 1 - cos(angle)
        abstracted as:
        rotated = scale + skew + offset
         */
        Vec3d scale = v.multiply(Math.cos(angle));
        Vec3d skew = v.crossProduct(axis).multiply(Math.sin(angle));
        Vec3d offset = axis.multiply(v.dotProduct(axis)).multiply(1 - Math.cos(angle));
        return scale.add(skew).add(offset);
    }
}
