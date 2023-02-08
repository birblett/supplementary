package com.birblett.mixin.events;

import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEventMixin {

    @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;"),
                    index = 2)
    private Vec3d onTravelEvent(Vec3d velocity) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                Vec3d newVelocity =  componentKey.get(self).onTravel(self, componentKey.get(self).getValue(), velocity);
                if (newVelocity != velocity) {
                    self.setVelocity(newVelocity);
                    self.velocityModified = true;
                    return newVelocity;
                }
            }
        }
        return velocity;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;shouldFall()Z"))
    private void inBlockTickEvent(CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                componentKey.get(self).inBlockTick(self, componentKey.get(self).getValue());
            }
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void onBlockHitEvent(BlockHitResult blockHitResult, CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                componentKey.get(self).onBlockHit(blockHitResult, self, componentKey.get(self).getValue());
            }
        }
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void preEntityHitEvent(EntityHitResult entityHitResult, CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                componentKey.get(self).preEntityHit(entityHitResult.getEntity(), self, componentKey.get(self).getValue());
            }
        }
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void postEntityHitEvent(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity entity = entityHitResult.getEntity();
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                componentKey.get(self).postEntityHit(entity, self, componentKey.get(self).getValue());
            }
        }
    }
}
