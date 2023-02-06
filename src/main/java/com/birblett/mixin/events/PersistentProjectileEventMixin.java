package com.birblett.mixin.events;

import com.birblett.lib.components.LevelComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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
    private Vec3d travel(Vec3d velocity) {
        for (ComponentKey<LevelComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                Vec3d newVelocity =  componentKey.get(self).onProjectileTick(self, componentKey.get(self).getValue(), velocity);
                if (newVelocity != velocity) {
                    self.setVelocity(newVelocity);
                    self.velocityModified = true;
                    return newVelocity;
                }
            }
        }
        return velocity;
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void entityHitEvent(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity entity = entityHitResult.getEntity();
        for (ComponentKey<LevelComponent> componentKey : SupplementaryComponents.PROJECTILE_COMPONENTS) {
            PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
            if (componentKey.get(self).getValue() > 0) {
                componentKey.get(self).onEntityHit(entity, self, componentKey.get(self).getValue());
            }
        }
    }
}