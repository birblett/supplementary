package com.birblett.mixin.enchantments.bunny_hop;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public class BunnyHopEntityMixin {
    /*
    Allows for scaling low jump gaps with bunnyhop enchant
     */

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"))
    private static void getCollidingEntity(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir) {
        if (entity instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BUNNYHOP, self) > 0 &&
                self.getVelocity().y < 0) {
            self.setOnGround(true);
        }
    }
}
