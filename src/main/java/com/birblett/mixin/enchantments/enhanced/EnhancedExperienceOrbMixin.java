package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

/**
 * Mending gear with Enhanced will grant half of their used exp to the player
 */
@Mixin(ExperienceOrbEntity.class)
public class EnhancedExperienceOrbMixin {

    @Unique int supplementary$SavedExpAmount;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void setSavedExpAmount(PlayerEntity player, CallbackInfo ci) {
        this.supplementary$SavedExpAmount = 0;
    }

    @Inject(method = "repairPlayerGears", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setSavedExpAmount(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir, Map.Entry<EquipmentSlot, ItemStack> entry, ItemStack itemStack, int i) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, itemStack) > 0) {
            this.supplementary$SavedExpAmount += i / 2;
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "onPlayerCollision", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/ExperienceOrbEntity;repairPlayerGears(Lnet/minecraft/entity/player/PlayerEntity;I)I"),
            index = 2)
    private int addSavedExpAmount(int amount) {
        return amount + this.supplementary$SavedExpAmount / 2;
    }
}
