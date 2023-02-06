package com.birblett.mixin.events;

import com.birblett.lib.components.IntComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickEvent(CallbackInfo ci) {
        for (ComponentKey<IntComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
            componentKey.get((LivingEntity) (Object) this).onTick((LivingEntity) (Object) this);
        }
    }
}
