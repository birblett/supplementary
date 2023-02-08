package com.birblett.mixin.events;

import com.birblett.Supplementary;
import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEntityTickEvent(CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
            componentKey.get((LivingEntity) (Object) this).onTick((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onHandSwingEvent(Hand hand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
            if (componentKey.get(livingEntity).getValue() > 0 || componentKey.get(livingEntity).getEntity() != null) {
                componentKey.get(livingEntity).onHandSwingEvent((LivingEntity) (Object) this, hand);
            }
        }
        if ((LivingEntity) (Object) this instanceof PlayerEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PLAYER_TICKING_COMPONENTS) {
                if (componentKey.get(livingEntity).getValue() > 0 || componentKey.get(livingEntity).getEntity() != null) {
                    componentKey.get((LivingEntity) (Object) this).onHandSwingEvent((LivingEntity) (Object) this, hand);
                }
            }
        }
    }
}
