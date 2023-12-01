package com.birblett.mixin.enchantments.haunted;

import com.birblett.registry.SupplementaryComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Phantoms spawned around Haunted players no longer take fire damage
 */
@Mixin(Entity.class)
public class HauntedEntityMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void disableFireDamageForHaunted(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PhantomEntity phantom && damageSource.isIn(DamageTypeTags.IS_FIRE)) {
            SupplementaryComponents.HAUNTED.maybeGet(phantom).ifPresent(component -> {
                if (component.getValue() > 0) {
                    cir.setReturnValue(true);
                }
            });
        }
    }
}
