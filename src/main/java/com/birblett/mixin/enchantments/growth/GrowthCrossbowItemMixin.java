package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for Growth to be applied to crossbow pull time
 */
@Mixin(CrossbowItem.class)
public class GrowthCrossbowItemMixin {

    @Inject(method = "getPullTime", at = @At("RETURN"), cancellable = true)
    private static void getStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, stack) > 0) {
            cir.setReturnValue((int) (cir.getReturnValue() / (SupplementaryEnchantmentHelper.getGrowthStat(stack, SupplementaryEnchantmentHelper.GrowthKey.DRAW_SPEED) + 1)));
        }
    }
}
