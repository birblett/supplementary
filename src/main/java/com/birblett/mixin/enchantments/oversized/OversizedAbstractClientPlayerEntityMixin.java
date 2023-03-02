package com.birblett.mixin.enchantments.oversized;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class OversizedAbstractClientPlayerEntityMixin {

    /**
     * @whyhere FOV scaled based on current draw speed modifier
     */
    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE"), index = 4)
    private float scaledDrawSpeedFOV(float fovScale) {
        return SupplementaryEnchantmentHelper.getDrawspeedModifier(fovScale, ((AbstractClientPlayerEntity) (Object) this).getActiveItem());
    }
}
