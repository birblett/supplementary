package com.birblett.mixin.attributes.no_fov_move_speed;

import com.birblett.registry.SupplementaryAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class NoFovMoveSpeedEntityMixin {

    @ModifyVariable(method = "updateVelocity", at = @At("HEAD"), index = 1, argsOnly = true)
    private float modifiedMoveSpeed(float v) {
        Entity self = (Entity) (Object) this;
        if (self instanceof LivingEntity entity && entity.getAttributeInstance(SupplementaryAttributes.NO_FOV_MOVE_SPEED)
                != null) {
            v *= entity.getAttributeValue(SupplementaryAttributes.NO_FOV_MOVE_SPEED) / 10.0;
        }
        return v;
    }

}
