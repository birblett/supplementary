package com.birblett.mixin.attributes.draw_speed;

import com.birblett.registry.SupplementaryAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Scale FOV based on current draw speed modifier
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class DrawSpeedAbstractClientPlayerEntityMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE", ordinal = 0), index = 4)
    private float scaledDrawSpeedFOV(float fovScale) {
        AbstractClientPlayerEntity p = (AbstractClientPlayerEntity) (Object) this;
        if (p.getAttributeInstance(SupplementaryAttributes.DRAW_SPEED) != null) {
            return (float) (fovScale * p.getAttributeValue(SupplementaryAttributes.DRAW_SPEED) / 10.0f);
        }
        return fovScale;
    }
}
