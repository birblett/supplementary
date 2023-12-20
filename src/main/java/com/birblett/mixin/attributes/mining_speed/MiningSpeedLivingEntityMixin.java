package com.birblett.mixin.attributes.mining_speed;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryAttributes;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales hand swing progress with mining speed
 */
@Mixin(LivingEntity.class)
public class MiningSpeedLivingEntityMixin {

    @Shadow public float handSwingProgress;

    @Inject(method = "tickHandSwing", at = @At("TAIL"))
    private void modifyHandSwing(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getAttributeInstance(SupplementaryAttributes.MINING_SPEED) != null) {
            this.handSwingProgress *= self.getAttributeValue(SupplementaryAttributes.MINING_SPEED) / 10.0f;
        }
    }

}
