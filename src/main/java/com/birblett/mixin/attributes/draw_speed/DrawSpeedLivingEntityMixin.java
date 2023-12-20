package com.birblett.mixin.attributes.draw_speed;

import com.birblett.registry.SupplementaryAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Attaches the draw speed attribute to living entity instances.
 */
@Mixin(LivingEntity.class)
public class DrawSpeedLivingEntityMixin {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void addMiningSpeedAttribute(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(SupplementaryAttributes.DRAW_SPEED);
    }

}
