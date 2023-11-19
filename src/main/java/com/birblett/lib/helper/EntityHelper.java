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
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Helper functions and constants related to entities.
 */
public class EntityHelper {

    public static final UUID ITEM_ATTACK_RANGE_MODIFIER_ID = UUID.fromString("4fe42e97-9447-41f2-8f83-b7e6a8ab9d5b");
    public static final UUID ITEM_REACH_MODIFIER_ID = UUID.fromString("7a443b73-b375-4499-b0d9-ea79179c16bf");

    /**
     * Checks if the given entity's hitbox intersects the hitbox of a block, with a certain degree of tolerance.
     * @param self Checked entity
     * @param xTolerance X-axis tolerance
     * @param yTolerance Y-axis tolerance
     * @param zTolerance Z-axis tolerance
     * @return Whether there exists a block that satisfies the collision check with the given tolerance
     */
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

    /**
     * Returns the result of {@link EntityHelper#isTouchingBlock(Entity, double, double, double)} with the same
     * tolerance value for all axes.
     * @param self Checked entity
     * @param tolerance X/Y/Z tolerance
     * @return Whether there exists a block that satisfies the collision check with the given tolerance
     */
    public static boolean isTouchingBlock(Entity self, double tolerance) {
        return isTouchingBlock(self, tolerance, tolerance, tolerance);
    }

    /**
     * A slightly modified {@link net.minecraft.entity.projectile.ProjectileUtil#getEntityCollision(World, Entity,
     * Vec3d, Vec3d, Box, Predicate, float)}. Returns a list of entities as opposed to the first hit result.
     * @param world Provided entity world
     * @param entity Source entity
     * @param min Raycast min bound for checking collision
     * @param max Raycast max bound for checking collision
     * @param box Provided hitbox
     * @param predicate A predicate colliding entities must satisfy
     * @param tolerance How much tolerance to use when calculating collision
     * @return A list of colliding entities
     */
    public static List<EntityHitResult> getEntityCollisions(World world, Entity entity, Vec3d min, Vec3d max, Box box,
                                                            Predicate<Entity> predicate, float tolerance) {
        double d = Double.MAX_VALUE;
        List<EntityHitResult> list = new ArrayList<>();
        for (Entity entityInBox : world.getOtherEntities(entity, box, predicate)) {
            double e;
            Box box2 = entityInBox.getBoundingBox().expand(tolerance);
            Optional<Vec3d> optional = box2.raycast(min, max);
            if (optional.isEmpty() || !((e = min.squaredDistanceTo(optional.get())) < d)) continue;
            list.add(new EntityHitResult(entityInBox));
            d = e;
        }
        return list;
    }
}
