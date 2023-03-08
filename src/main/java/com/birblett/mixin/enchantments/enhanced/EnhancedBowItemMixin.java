package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Makes Tipped and Spectral arrows valid for Infinity enchant when paired with Enhanced
 */
@Mixin(BowItem.class)
public class EnhancedBowItemMixin {

    @Unique private boolean supplementary$HasEnhanced;

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
            ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getEnhanced(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack) {
        this.supplementary$HasEnhanced = EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, stack) > 0 &&
                itemStack.getItem() instanceof ArrowItem;
    }

    @ModifyVariable(method = "onStoppedUsing", at = @At(value = "STORE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"),
            ordinal = 1)
    private boolean applyInfinity(boolean isArrow) {
        return isArrow || this.supplementary$HasEnhanced;
    }
}
