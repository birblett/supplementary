package com.birblett.mixin.items;

import com.birblett.Supplementary;
import com.birblett.trinkets.CapeItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for cape items to be placed in a Loom
 */
@Mixin(targets = "net/minecraft/screen/LoomScreenHandler$3")
public class LoomScreenHandlerInnerMixin {

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void allowCapeLoomRecipe(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof CapeItem) {
            cir.setReturnValue(true);
        }
    }
}
