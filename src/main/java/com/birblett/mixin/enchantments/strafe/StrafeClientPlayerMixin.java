package com.birblett.mixin.enchantments.strafe;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class StrafeClientPlayerMixin {

    @Unique private int supplementary$StrafeState = 0;
    @Unique private int supplementary$StrafeDir = 0;

    private int getSideInput(ClientPlayerEntity self) {
        return (self.input.pressingLeft ? 1 : 0) + (self.input.pressingRight ? 2 : 0);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void allowAirDash(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.STRAFE, self) > 0) {
            if (self.isOnGround() || self.isTouchingWater() || self.isFallFlying()) {
                int input = getSideInput(self);
                if (!(input == 1 || input == 2) && this.supplementary$StrafeState % 2 == 0) {
                    this.supplementary$StrafeState += 1;
                }
                switch (this.supplementary$StrafeState) {
                    case 1 -> {
                        if (input == 1 || input == 2) {
                            this.supplementary$StrafeState = 2;
                            this.supplementary$StrafeDir = input;
                            self.ticksLeftToDoubleTapSprint = 7;
                        }
                    }
                    case 3 -> {
                        if (input == this.supplementary$StrafeDir && self.ticksLeftToDoubleTapSprint > 0) {
                            double rotation = Math.toRadians(self.getRotationClient().y);
                            double strafeStrength = 2;
                            double dy = 0;
                            if (self.isSubmergedInWater()) {
                                strafeStrength = 1;
                            }
                            if (self.isFallFlying()) {
                                dy = 0.8 * self.getVelocity().y;
                                strafeStrength = 1.5;
                            }
                            double dir = -strafeStrength * (input - 1.5);
                            self.setVelocity(new Vec3d(dir * Math.cos(rotation), dy, dir * Math.sin(rotation)).add(self.getVelocity().multiply(0.2)));
                            self.ticksLeftToDoubleTapSprint = 0;
                            this.supplementary$StrafeDir = 0;
                            this.supplementary$StrafeState = 0;
                        }
                        else if (self.ticksLeftToDoubleTapSprint <= 0) {
                            this.supplementary$StrafeDir = 0;
                            this.supplementary$StrafeState = 0;
                        }
                    }
                }
            }
            else {
                this.supplementary$StrafeDir = 0;
                this.supplementary$StrafeState = 0;
            }
        }
    }
}
