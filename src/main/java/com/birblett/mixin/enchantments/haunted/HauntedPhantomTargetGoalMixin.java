package com.birblett.mixin.enchantments.haunted;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

/**
 * Makes phantoms prioritize players with Haunted gear.
 */
@Mixin(PhantomEntity.FindTargetGoal.class)
public class HauntedPhantomTargetGoalMixin {

    @Shadow @Final PhantomEntity field_7319;

    @Inject(method = "canStart", at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void targetHauntedPlayers(CallbackInfoReturnable<Boolean> cir, List<PlayerEntity> list) {
        PlayerEntity bestHauntedTarget = null;
        int bestHauntedLevel = 0;
        for (PlayerEntity p : list) {
            if (!field_7319.isTarget(p, TargetPredicate.DEFAULT)) continue;
            if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.HAUNTED, p) > bestHauntedLevel) {
                bestHauntedTarget = p;
            }
        }
        if (bestHauntedTarget != null) {
            field_7319.setTarget(bestHauntedTarget);
            cir.setReturnValue(true);
        }
    }
}
