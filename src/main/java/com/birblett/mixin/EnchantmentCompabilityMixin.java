package com.birblett.mixin;

import com.birblett.Supplementary;
import com.birblett.items.SupplementaryEnchantable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentCompabilityMixin {

    @Inject(method = "isAcceptableItem", at = @At("TAIL"), cancellable = true)
    private void makeItemCompatible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        /*
        allows SupplementaryEnchantable interface to supply valid Enchantment subclasses for items
         */
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof SupplementaryEnchantable enchantable) {
            for (Class<? extends Enchantment> enchant : enchantable.getValidEnchantments()) {
                if (enchant.isInstance(self)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
