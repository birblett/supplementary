package com.birblett.mixin.enchantments.acrobatic;

import com.birblett.lib.helper.EntityHelper;
import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows for double jumps and wall clings with the Acrobatics enchantment. Can probably be moved to an event callback?
 */
@Mixin(ClientPlayerEntity.class)
public class AcrobaticClientPlayerMixin {

    @Unique private int airJumps;
    @Unique private int wallClingTicks;
    @Unique private boolean storedJump;
    @Unique private boolean canAirJump;
    @Unique private Vec3d initialPos;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void airJump(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.ACROBATIC, self) > 0 && !self.noClip) {
            Vec3d velocity;
            if (!self.isOnGround() && !self.isTouchingWater() && (velocity = self.getVelocity()).y < 0.2 &&
                    this.airJumps > 0) {
                if (self.input.jumping && this.canAirJump) {
                    self.setVelocity(velocity.x, 0, velocity.z);
                    self.setOnGround(true);
                    this.airJumps -= 2;
                    this.canAirJump = false;
                }
                else if (!self.input.jumping) {
                    this.canAirJump = true;
                }
            }
            else if (self.isOnGround()){
                this.airJumps = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ACROBATIC, self) > 0 ? 6 : 4;
                this.wallClingTicks = 30;
                this.storedJump = false;
                this.canAirJump = false;
            }
            if (EntityHelper.isTouchingBlock(self, 0.01, 0, 0.01) && this.wallClingTicks > 0 &&
                    !self.isOnGround() && this.airJumps > 0) {
                if (self.input.sneaking) {
                    if (this.wallClingTicks == 30) {
                        this.initialPos = self.getPos();
                    }
                    else {
                        self.setPosition(this.initialPos);
                    }
                    this.wallClingTicks--;
                    self.setVelocity(0, 0, 0);
                    self.input.movementForward = 0;
                    self.input.movementSideways = 0;
                    this.storedJump = true;
                }
                else if (this.storedJump){
                    this.wallClingTicks = 30;
                    double velocityMult = SupplementaryEnchantmentHelper.getEnhancedEquipLevel(SupplementaryEnchantments.ACROBATIC, self) > 0 ? 0.6 : 0.4;
                    Vec3d horizontalComponent = self.getRotationVector().multiply(1, 0, 1).normalize().multiply(velocityMult);
                    self.setVelocity(horizontalComponent.x, 0.6, horizontalComponent.z);
                    this.storedJump = false;
                    this.airJumps--;
                }
            }
        }
    }
}
