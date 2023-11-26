package com.birblett.mixin.enchantments.oversized;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Scale FOV based on current draw speed modifier
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class OversizedAbstractClientPlayerEntityMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE", ordinal = 0), index = 4)
    private float scaledDrawSpeedFOV(float fovScale) {
        return Math.min(1.0f, SupplementaryEnchantmentHelper.getDrawspeedModifier((LivingEntity) (Object) this, fovScale, ((AbstractClientPlayerEntity) (Object) this).getActiveItem()));
    }
}
