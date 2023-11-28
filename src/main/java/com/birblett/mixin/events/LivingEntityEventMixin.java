package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
import com.birblett.registry.SupplementaryComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventMixin {

    @Unique private static DamageSource supplementary$DamageSource;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEntityTickEvent(CallbackInfo ci) {
        EntityEvents.ENTITY_GENERIC_TICK.invoker().onEntityTick((LivingEntity) (Object) this);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onEntityTravelTickEvent(CallbackInfo ci) {
        LivingEntity self = ((LivingEntity) (Object) this);
        self.setVelocity(EntityEvents.LIVING_ENTITY_TRAVEL_TICK.invoker().onTravelTick(self, self.getVelocity()));
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onHandSwingEvent(Hand hand, CallbackInfo ci) {
        EntityEvents.SWING_HAND_EVENT.invoker().onHandSwing((LivingEntity) (Object) this, hand);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    private void getDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        supplementary$DamageSource = source;
    }

    @ModifyVariable(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"),
            argsOnly = true)
    private float onDamageEvent(float amount) {
        if (!((LivingEntity) (Object) this).getWorld().isClient()) {
            amount += EntityEvents.LIVING_ENTITY_ADDITIVE_DAMAGE_EVENT.invoker().onDamage((LivingEntity) (Object) this,
                    supplementary$DamageSource, amount);
            amount *= EntityEvents.LIVING_ENTITY_MULTIPLICATIVE_DAMAGE_EVENT.invoker().onDamage((LivingEntity) (Object) this,
                    supplementary$DamageSource, amount);
        }
        return amount;
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (!((LivingEntity) (Object) this).getWorld().isClient()) {
            EntityEvents.LIVING_ENTITY_DEATH_EVENT.invoker().onDeath((LivingEntity) (Object) this, damageSource);
        }
    }
}
