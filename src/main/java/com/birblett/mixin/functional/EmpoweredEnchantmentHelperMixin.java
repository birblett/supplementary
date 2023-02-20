package com.birblett.mixin.functional;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EmpoweredEnchantmentHelperMixin {
    /*
    Modifies the effective level of enchantments to be +1
     */

    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void modifyLevel(Enchantment enchantment, ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment != SupplementaryEnchantments.EMPOWERED && cir.getReturnValue() > 0 &&
                EnchantmentHelper.getLevel(SupplementaryEnchantments.EMPOWERED, itemStack) > 0) {
            cir.setReturnValue(cir.getReturnValue() + 1);
        }
    }

    @Inject(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true)
    private static void forEachEnchantmentOverride(EnchantmentHelper.Consumer consumer, ItemStack itemStack, CallbackInfo ci) {
        /*
        completely overrides the method due to functionality being impossible to implement via modifyarg/variable
         */
        if (itemStack.isEmpty()) {
            return;
        }
        int extraLevel = EnchantmentHelper.getLevel(SupplementaryEnchantments.EMPOWERED, itemStack) > 0 ? 1 : 0;
        NbtList nbtList = itemStack.getEnchantments();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(enchantment ->
                    consumer.accept(enchantment, EnchantmentHelper.getLevelFromNbt(nbtCompound) + extraLevel));
        }
        ci.cancel();
    }
}
