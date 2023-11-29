package com.birblett.mixin.enchantments.haunted;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Greatly decreases Phantom attack cooldown when targeting an entity with Haunted gear.
 */
@Mixin(PhantomEntity.StartAttackGoal.class)
public class HauntedPhantomAttackGoalMixin {

    @Shadow @Final PhantomEntity field_7321;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PhantomEntity$StartAttackGoal;getTickCount(I)I"))
    private int modifyAttackCooldown(int cd) {
        if (field_7321.getTarget() != null && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.HAUNTED, field_7321.getTarget()) > 0) {
            cd -= 140;
        }
        return cd;
    }
}
