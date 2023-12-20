package com.birblett.mixin.enchantments.mole;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows players with Mole enchant to automatically crouch near 1-block gaps, and also allows ascending through 1-block
 * gaps while All Terrain gear is equipped.
 */
@Mixin(Entity.class)
public class MoleEntityMixin {

    @Inject(method = "updateSwimming", at = @At("TAIL"), cancellable = true)
    private void setCrawling(CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PlayerEntity p && !p.isTouchingWater() && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.MOLE,
                p) > 0) {
            Vec2f vel = p.getRotationClient();
            double mul = p.isSprinting() ? 0.3 : 0.1;
            double x = -Math.sin(Math.toRadians(vel.y)) * mul;
            double z = Math.cos(Math.toRadians(vel.y)) * mul;
            Box b = p.getBoundingBox(EntityPose.STANDING).offset(p.getPos()).offset(x, 0, z);
            Box b2 = p.getBoundingBox(EntityPose.STANDING).offset(p.getPos()).offset(x, 0, 0);
            Box b3 = p.getBoundingBox(EntityPose.STANDING).offset(p.getPos()).offset(0, 0, z);
            boolean collidesDirect = p.isOnGround() && !p.getWorld().canCollide(p, b.withMaxY(b.minY + 1)) && p.getWorld()
                    .canCollide(p, b.withMinY(b.maxY - 0.7));
            boolean collidesIndirect1 = p.isOnGround() && !collidesDirect && !p.getWorld().canCollide(p, b2.withMaxY(b2.minY
                    + 1)) && p.getWorld().canCollide(p, b2.withMinY(b2.maxY - 0.7));
            boolean collidesIndirect2 = p.isOnGround() && !collidesIndirect1 && !p.getWorld().canCollide(p, b3.withMaxY(
                    b3.minY + 1)) && p.getWorld().canCollide(p, b3.withMinY(b3.maxY - 0.7));
            if (collidesDirect || collidesIndirect1 || collidesIndirect2) {
                p.setSwimming(true);
                ci.cancel();
            }
            if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ALL_TERRAIN, p) > 0) {
                Box b4 = b.offset(0, 1, 0);
                Box b5 = b2.offset(0, 1, 0);
                Box b6 = b3.offset(0, 1, 0);
                collidesDirect = p.isOnGround() && !p.getWorld().canCollide(p, b4.withMaxY(b4.minY + 1)) && p.getWorld()
                        .canCollide(p, b4.withMinY(b4.maxY - 0.7)) && p.getWorld().canCollide(p, b.withMaxY(b.minY + 1));
                collidesIndirect1 = p.isOnGround() && !collidesDirect && !p.getWorld().canCollide(p, b5.withMaxY(b5.minY
                        + 1)) && p.getWorld().canCollide(p, b5.withMinY(b5.maxY - 0.7)) && p.getWorld().canCollide(p, b2
                        .withMaxY(b2.minY + 1));
                collidesIndirect2 = p.isOnGround() && !collidesIndirect1 && !p.getWorld().canCollide(p, b6.withMaxY(
                        b6.minY + 1)) && p.getWorld().canCollide(p, b6.withMinY(b3.maxY - 0.7)) && p.getWorld().canCollide(
                                p, b3.withMaxY(b3.minY + 1));
                if (collidesDirect || collidesIndirect1 || collidesIndirect2) {
                    p.setSwimming(true);
                    ci.cancel();
                }
            }
        }
    }

}
