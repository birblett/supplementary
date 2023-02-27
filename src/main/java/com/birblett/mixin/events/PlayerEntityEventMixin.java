package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public class PlayerEntityEventMixin {

    @Unique private float damageModifier;

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void attackEvent(Entity target, CallbackInfo ci, float damageAmount, float enchantmentDamageBoosts,
                             float attackCooldownProgress, boolean isMaxCharge, boolean sprintingWhileMaxCharge,
                             int knockbackAmount, boolean isCritical, boolean unused, double speedDelta,
                             float healthBeforeAttack, boolean setOnFire, int fireAspectLevel, Vec3d targetVelocity) {
        float currentDamage = EntityEvents.LIVING_ENTITY_ATTACK_EVENT.invoker().onAttack((PlayerEntity) (Object) this, target,
                damageAmount, isCritical);
        this.damageModifier = currentDamage - damageAmount;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float setDamage(float damage) {
        return damage + this.damageModifier;
    }
}
