package com.birblett.mixin;

import com.birblett.trinkets.CapeItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/LoomScreenHandler$3")
public class LoomScreenHandlerMixin {

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void allowCapeLoomRecipe(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // TODO: get shift click working
        if (stack.getItem() instanceof CapeItem) cir.setReturnValue(true);
    }
}
