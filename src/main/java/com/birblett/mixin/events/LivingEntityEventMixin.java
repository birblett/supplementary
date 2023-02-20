package com.birblett.mixin.events;

import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventMixin {

    @Unique private DamageSource supplementary$DamageSource;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEntityTickEvent(CallbackInfo ci) {
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
            componentKey.get((LivingEntity) (Object) this).onTick((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onHandSwingEvent(Hand hand, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.ENTITY_TICKING_COMPONENTS) {
            if (componentKey.get(self).getValue() > 0 || componentKey.get(self).getEntity() != null) {
                componentKey.get(self).onHandSwingEvent((LivingEntity) (Object) this, hand);
            }
        }
        if (self instanceof PlayerEntity) {
            for (ComponentKey<BaseComponent> componentKey : SupplementaryComponents.PLAYER_TICKING_COMPONENTS) {
                if (componentKey.get(self).getValue() > 0 || componentKey.get(self).getEntity() != null) {
                    componentKey.get(self).onHandSwingEvent(self, hand);
                }
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void getDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.supplementary$DamageSource = source;
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float augmentDamage(float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        for (ItemStack itemStack : self.getArmorItems()) {
            amount = processEnchantments(self, itemStack, amount, EquipmentSlot.Type.ARMOR);
        }
        amount = processEnchantments(self, self.getMainHandStack(), amount, EquipmentSlot.Type.HAND);
        amount = processEnchantments(self, self.getOffHandStack(), amount, EquipmentSlot.Type.HAND);
        return amount;
    }

    @Unique private float processEnchantments(LivingEntity self, ItemStack itemStack, float amount, EquipmentSlot.Type type) {
        for (Map.Entry<Enchantment, Integer> enchantmentEntry: EnchantmentHelper.get(itemStack).entrySet()) {
            if (enchantmentEntry.getKey() instanceof EnchantmentBuilder enchantmentBuilder) {
                amount += enchantmentBuilder.onDamage(self, this.supplementary$DamageSource, enchantmentEntry.getValue(), amount, type);
            }
        }
        return amount;
    }
}
