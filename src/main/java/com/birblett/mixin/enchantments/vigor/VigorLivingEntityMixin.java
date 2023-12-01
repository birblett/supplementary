package com.birblett.mixin.enchantments.vigor;

import com.birblett.registry.SupplementaryComponents;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles one-time revive for Vigor enchantment.
 */
@Mixin(LivingEntity.class)
public class VigorLivingEntityMixin {

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z"))
    private void vigorRevive(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.VIGOR, self) > 0 && self.isDead()) {
            SupplementaryComponents.VIGOR.maybeGet(self).ifPresent(component -> {
                if (!(boolean) component.getValue()) {
                    //noinspection unchecked
                    component.setValue(true);
                    self.setHealth(1.0f);
                    self.clearStatusEffects();
                    self.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 2));
                    self.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 2));
                }
            });
        }
    }
}
