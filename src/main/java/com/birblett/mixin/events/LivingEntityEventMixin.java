package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
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

    @Unique private DamageSource supplementary$DamageSource;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEntityTickEvent(CallbackInfo ci) {
        EntityEvents.ENTITY_GENERIC_TICK.invoker().onEntityTick((LivingEntity) (Object) this);
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onHandSwingEvent(Hand hand, CallbackInfo ci) {
        EntityEvents.SWING_HAND_EVENT.invoker().onHandSwing((LivingEntity) (Object) this, hand);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void getDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.supplementary$DamageSource = source;
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float onDamageEvent(float amount) {
        if (!((LivingEntity) (Object) this).getWorld().isClient()) {
            amount += EntityEvents.MODIFY_DAMAGE_EVENT.invoker().onDamage((LivingEntity) (Object) this, this.supplementary$DamageSource, amount);
        }
        return amount;
    }
}
