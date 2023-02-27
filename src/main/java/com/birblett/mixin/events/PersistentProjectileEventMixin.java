package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
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
        velocity = EntityEvents.PROJECTILE_TRAVEL_TICK.invoker().onTravelTick((PersistentProjectileEntity) (Object) this, velocity);
        return velocity;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;shouldFall()Z"))
    private void inBlockTickEvent(CallbackInfo ci) {
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.invoker().onEntityTick((PersistentProjectileEntity) (Object) this);
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void onBlockHitEvent(BlockHitResult blockHitResult, CallbackInfo ci) {
        EntityEvents.ARROW_BLOCK_HIT_EVENT.invoker().onHitEvent(blockHitResult, (PersistentProjectileEntity) (Object) this);
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void preEntityHitEvent(EntityHitResult entityHitResult, CallbackInfo ci) {
        EntityEvents.ARROW_PRE_ENTITY_HIT_EVENT.invoker().onHitEvent(entityHitResult, (PersistentProjectileEntity) (Object) this);
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void postEntityHitEvent(EntityHitResult entityHitResult, CallbackInfo ci) {
        EntityEvents.ARROW_POST_ENTITY_HIT_EVENT.invoker().onHitEvent(entityHitResult, (PersistentProjectileEntity) (Object) this);
    }
}
