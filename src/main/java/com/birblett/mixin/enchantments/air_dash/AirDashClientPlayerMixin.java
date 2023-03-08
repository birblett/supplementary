package com.birblett.mixin.enchantments.air_dash;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows double tap forward in the air for an air dash
 */
@Mixin(ClientPlayerEntity.class)
public class AirDashClientPlayerMixin {

    @Unique private int supplementary$AirDashState = 0;
    @Unique private int supplementary$AirDashes;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void allowAirDash(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.AIR_DASH, self) > 0) {
            if (!self.isOnGround() && !self.isTouchingWater()) {
                if (this.supplementary$AirDashes > 0) {
                    if (!self.input.hasForwardMovement() && this.supplementary$AirDashState % 2 == 0) {
                        this.supplementary$AirDashState += 1;
                    }
                    switch (this.supplementary$AirDashState) {
                        case 1 -> {
                            if (self.input.hasForwardMovement()) {
                                this.supplementary$AirDashState = 2;
                                self.ticksLeftToDoubleTapSprint = 7;
                            }
                        }
                        case 3 -> {
                            if (self.input.hasForwardMovement() && self.ticksLeftToDoubleTapSprint > 0) {
                                self.setVelocity(self.getRotationVector().multiply(1.05));
                                self.ticksLeftToDoubleTapSprint = 0;
                                this.supplementary$AirDashes--;
                                this.supplementary$AirDashState = 0;
                            }
                        }
                    }
                }
            }
            else {
                this.supplementary$AirDashes = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.AIR_DASH, self) > 0 ? 2 : 1;
                this.supplementary$AirDashState = 0;
            }
        }
    }
}
