package com.birblett.mixin;

import com.birblett.items.SupplementaryEnchantable;
import com.birblett.lib.creational.ContractBuilder;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows SupplementaryEnchantable interface to supply valid Enchantment subclasses for items, as well as enabling
 * gold coloration for contracts
 */
@Mixin(Enchantment.class)
public class EnchantmentCompatMixin {

    @Inject(method = "isAcceptableItem", at = @At("TAIL"), cancellable = true)
    private void makeItemCompatible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof SupplementaryEnchantable enchantable) {
            for (Class<? extends Enchantment> enchant : enchantable.getValidEnchantments()) {
                if (enchant.isInstance(self)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyArg(method = "getName", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;",
            ordinal = 1))
    private Formatting replaceContractFormat(Formatting formatting) {
        if ((Object) this instanceof ContractBuilder) {
            formatting = Formatting.GOLD;
        }
        return formatting;
    }
}
