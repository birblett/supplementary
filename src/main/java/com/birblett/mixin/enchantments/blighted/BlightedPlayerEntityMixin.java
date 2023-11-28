package com.birblett.mixin.enchantments.blighted;

import com.birblett.registry.SupplementaryComponents;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents players with blighted effect from healing. Also applies semi-permanent blight for level 2 Blight. Reset on death
 * in {@link com.birblett.mixin.events.LivingEntityEventMixin#onDeathEvent(DamageSource, CallbackInfo)}
 */
@SuppressWarnings("unchecked")
@Mixin(PlayerEntity.class)
public class BlightedPlayerEntityMixin {

    @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    private void noFoodHealWithBlight(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        switch(EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.BLIGHTED, self)) {
            case 0 -> {}
            case 1 -> cir.setReturnValue(false);
            default -> SupplementaryComponents.BLIGHTED.maybeGet(self).ifPresent(component -> component.setValue(true));
        }
        SupplementaryComponents.BLIGHTED.maybeGet(self).ifPresent(component -> {
            if ((boolean) component.getValue()) {
                cir.setReturnValue(false);
            }
        });
    }
}
