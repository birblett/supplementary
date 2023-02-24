package com.birblett.lib.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

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
}
