package com.birblett.mixin.functional;

import com.birblett.Supplementary;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class AirDashClientPlayerMixin {
    /*
    Air Dash input handling
     */

    @Unique private int airDashState = 0;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void allowAirDash(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.AIR_DASH, self) > 0) {
            if (!self.isOnGround() && !self.isTouchingWater()) {
                if (!self.input.hasForwardMovement() && this.airDashState % 2 == 0) {
                    this.airDashState += 1;
                }
                switch (this.airDashState) {
                    case 1 -> {
                        if (self.input.hasForwardMovement()) {
                            this.airDashState = 2;
                            self.ticksLeftToDoubleTapSprint = 7;
                        }
                    }
                    case 3 -> {
                        if (self.input.hasForwardMovement() && self.ticksLeftToDoubleTapSprint > 0) {
                            self.setVelocity(self.getRotationVector().multiply(0.9));
                            self.ticksLeftToDoubleTapSprint = 0;
                        }
                    }
                }
            }
            else {
                this.airDashState = 0;
            }
        }
    }
}
