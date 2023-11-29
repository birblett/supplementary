package com.birblett.mixin.enchantments.magic_guard;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables damage event if entity is wearing Magic Guard gear and damage is indirect
 */
@Mixin(Entity.class)
public class MagicGuardEntityMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void disableIndirectDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity self && damageSource.isIn(SupplementaryEnchantmentHelper.INDIRECT_DAMAGE) &&
                EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.MAGIC_GUARD,self) > 0) {
            cir.setReturnValue(true);
        }
    }
}
