package com.birblett.mixin.functional.render;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class OversizedAbstractClientPlayerMixin {

    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE"), index = 4)
    private float scaleOversizedFOV(float fovScale) {
        return SupplementaryEnchantmentHelper.getOversizedDrawspeedModifier(fovScale, ((AbstractClientPlayerEntity) (Object) this).getActiveItem());
    }
}
