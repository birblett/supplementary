package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
import com.birblett.lib.api.EventReturnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
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
        EntityEvents.PROJECTILE_GENERIC_TICK.invoker().onEntityTick((FishingBobberEntity) (Object) this);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 5))
    private Vec3d onTravelEvent(Vec3d velocity) {
        velocity = EntityEvents.PROJECTILE_TRAVEL_TICK.invoker().onTravelTick((FishingBobberEntity) (Object) this, velocity);
        return velocity;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPosition()V"))
    private void inBlockTick(CallbackInfo ci) {
        EntityEvents.PROJECTILE_IN_BLOCK_TICK.invoker().onEntityTick((FishingBobberEntity) (Object) this);

    }

    @Inject(method = "pullHookedEntity", at = @At("HEAD"), cancellable = true)
    private void onEntityReelEvent(Entity entity, CallbackInfo ci) {
        FishingBobberEntity self = ((FishingBobberEntity) (Object) this);
        if (EntityEvents.FISHING_BOBBER_REEL_EVENT.invoker().onReel(self, entity) != EventReturnable.NO_OP) {
            ci.cancel();
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;discard()V"), cancellable = true)
    private void onEmptyReelEvent(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        FishingBobberEntity self = (FishingBobberEntity) (Object) this;
        if (EntityEvents.FISHING_BOBBER_REEL_EVENT.invoker().onReel(self, null) != EventReturnable.NO_OP) {
            cir.setReturnValue(1);
        }
    }
}
