package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.EnchantHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Removes Growth NBT while using a Grindstone
 */
@Mixin(GrindstoneScreenHandler.class)
public class GrowthGrindstoneScreenHandlerMixin {

    @Inject(method = "grind", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;removeSubNbt(Ljava/lang/String;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void grindGrowth(ItemStack item, int damage, int amount, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        itemStack.removeSubNbt(EnchantHelper.GROWTH_NBT_KEY);
    }
}
