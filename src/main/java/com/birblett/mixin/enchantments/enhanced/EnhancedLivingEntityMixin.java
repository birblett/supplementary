package com.birblett.mixin.enchantments.enhanced;

import com.birblett.Supplementary;
import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryComponents;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Allows Depth Strider to work in lava, Soul Speed no longer consumes durability, Piercing arrows ignore some armor,
 * Protection also reduces knockback for respective damage types with Enhanced
 */
@Mixin(LivingEntity.class)
public class EnhancedLivingEntityMixin {

    @Unique private float supplementary$ArmorPierceAmount;
    @Unique private float supplementary$KnockbackMult;
    @Unique private boolean supplementary$ApplyKnockbackMult = false;

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V",
            ordinal = 1))
    private float updateLavaBaseSpeed(float speed) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ENHANCED, self) > 0) {
            float depthStrider = Math.min(EnchantmentHelper.getDepthStrider(self), 3.0f);
            if (depthStrider > 0.0f) {
                speed += (self.getMovementSpeed() - speed) * depthStrider / 3.0f;
            }
        }
        return speed;
    }

    @Inject(method = "addSoulSpeedBoostIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void damageAmount(CallbackInfo ci, int i, EntityAttributeInstance entityAttributeInstance, ItemStack itemStack) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, itemStack) > 0) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "applyArmorToDamage", at = @At( value = "HEAD", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"),
            ordinal = 0, argsOnly = true)
    private DamageSource getArmorPierce(DamageSource source) {
        this.supplementary$ArmorPierceAmount = 1;
        if (source instanceof ProjectileDamageSource projectileDamageSource && projectileDamageSource.getSource() instanceof ArrowEntity arrow &&
                SupplementaryComponents.ENHANCED.get(arrow).getValue() > 0) {
            this.supplementary$ArmorPierceAmount -= arrow.getPierceLevel() * 0.05f;
        }
        return source;
    }

    @ModifyArg(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"),
            index = 1)
    private float armorPiercing(float armor) {
        return armor * this.supplementary$ArmorPierceAmount;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void getKnockbackMultiplier(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.supplementary$ApplyKnockbackMult = (this.supplementary$KnockbackMult = SupplementaryEnchantmentHelper.enhancedProtKnockbackAmount((LivingEntity) (Object) this, source)) < 1.0f;
    }

    @ModifyVariable(method = "takeKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;"),
            index = 1, argsOnly = true)
    private double modifyKnockback(double strength) {
        if (this.supplementary$ApplyKnockbackMult) {
            return strength * this.supplementary$KnockbackMult;
        }
        return strength;
    }
}
