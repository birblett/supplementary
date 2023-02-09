package com.birblett.mixin.events;

import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEventMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickEvent(CallbackInfo ci) {
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                componentKey.get(self).onTick(self.getPlayerOwner());
            }
        }
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 5))
    private Vec3d onTravelEvent(Vec3d velocity) {
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                Vec3d newVelocity = componentKey.get(self).onTravel(self, componentKey.get(self).getValue(), velocity);
                if (velocity != newVelocity) {
                    self.setVelocity(newVelocity);
                    self.velocityModified = true;
                    velocity = newVelocity;
                }
            }
        }
        return velocity;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPosition()V"))
    private void inBlockTick(CallbackInfo ci) {
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        Box box = self.getBoundingBox().expand(0.02);
        double[] corners = {box.minX, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ};
        for (int xPos = 0; xPos < 2; xPos++) {
            for (int yPos = 2; yPos < 4; yPos++) {
                for (int zPos = 4; zPos < 6; zPos++) {
                    BlockPos corner = new BlockPos(corners[xPos], corners[yPos], corners[zPos]);
                    BlockState blockState = self.world.getBlockState(corner);
                    VoxelShape vs = blockState.getCollisionShape(self.world, corner, ShapeContext.of(self));
                    if (!vs.isEmpty() && vs.getBoundingBox().offset(corner).intersects(box)) {
                        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
                            if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                                componentKey.get(self).inBlockTick(self, componentKey.get(self).getValue());
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "pullHookedEntity", at = @At("HEAD"), cancellable = true)
    private void onEntityReelEvent(Entity entity, CallbackInfo ci) {
        boolean shouldReturn = false;
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                shouldReturn = shouldReturn || componentKey.get(self).postEntityHit(entity, self, 1);
            }
        }
        if (shouldReturn) {
            ci.cancel();
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;discard()V"), cancellable = true)
    private void onEmptyReelEvent(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        boolean shouldReturn = false;
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            if (componentKey.isProvidedBy(self) && componentKey.get(self).getValue() > 0) {
                shouldReturn = shouldReturn || componentKey.get(self).postEntityHit(null, self, 1);
            }
        }
        if (shouldReturn) {
            cir.setReturnValue(1);
        }
    }
}
