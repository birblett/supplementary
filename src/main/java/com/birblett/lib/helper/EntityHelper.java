package com.birblett.lib.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EntityHelper {

    public static boolean isTouchingBlock(Entity self, double tolerance) {
        return isTouchingBlock(self, tolerance, tolerance, tolerance);
    }

    public static boolean isTouchingBlock(Entity self, double xTolerance, double yTolerance, double zTolerance) {
        Box box = self.getBoundingBox().expand(xTolerance, yTolerance, zTolerance);
        double[] corners = {box.minX, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ};
        for (int xPos = 0; xPos < 2; xPos++) {
            for (int yPos = 2; yPos < 4; yPos++) {
                for (int zPos = 4; zPos < 6; zPos++) {
                    BlockPos corner = new BlockPos(corners[xPos], corners[yPos], corners[zPos]);
                    BlockState blockState = self.world.getBlockState(corner);
                    VoxelShape vs = blockState.getCollisionShape(self.world, corner, ShapeContext.of(self));
                    if (!vs.isEmpty() && vs.getBoundingBox().offset(corner).intersects(box)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<EntityHitResult> getEntityCollisions(World world, Entity entity, Vec3d min, Vec3d max, Box box,
                                                            Predicate<Entity> predicate, float f) {
        double d = Double.MAX_VALUE;
        List<EntityHitResult> list = new ArrayList<>();
        for (Entity entityInBox : world.getOtherEntities(entity, box, predicate)) {
            double e;
            Box box2 = entityInBox.getBoundingBox().expand(f);
            Optional<Vec3d> optional = box2.raycast(min, max);
            if (optional.isEmpty() || !((e = min.squaredDistanceTo(optional.get())) < d)) continue;
            list.add(new EntityHitResult(entityInBox));
            d = e;
        }
        return list;
    }
}
