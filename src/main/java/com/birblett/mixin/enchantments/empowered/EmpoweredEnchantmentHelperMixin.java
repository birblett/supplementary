package com.birblett.mixin.enchantments.empowered;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

/**
 * Modifies the effective level of other enchantments to be +1
 */
@Mixin(EnchantmentHelper.class)
public abstract class EmpoweredEnchantmentHelperMixin {

    @Unique private static NbtCompound supplementary$NbtCompound;
    @Unique private static EnchantmentHelper.Consumer supplementary$Consumer;
    @Unique private static boolean supplementary$HasEmpowered;

    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void modifyLevel(Enchantment enchantment, ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment != SupplementaryEnchantments.EMPOWERED && cir.getReturnValue() > 0 &&
                EnchantmentHelper.getLevel(SupplementaryEnchantments.EMPOWERED, itemStack) > 0) {
            cir.setReturnValue(cir.getReturnValue() + 1);
        }
    }

    @Inject(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;getOrEmpty(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getConsumer(EnchantmentHelper.Consumer consumer, ItemStack stack, CallbackInfo ci, NbtList nbtList, int i, NbtCompound nbtCompound) {
        supplementary$Consumer = consumer;
        supplementary$NbtCompound = nbtCompound;
        supplementary$HasEmpowered = EnchantmentHelper.getLevel(SupplementaryEnchantments.EMPOWERED, stack) > 0;
    }

    @ModifyArg(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static Consumer<Enchantment> addEmpoweredBoost(Consumer<Enchantment> consumer) {
        if (supplementary$HasEmpowered) {
            return enchantment -> supplementary$Consumer.accept(enchantment, EnchantmentHelper.getLevelFromNbt(supplementary$NbtCompound) +
                    (enchantment.getMaxLevel() > 1 ? 1 : 0));
        }
        return consumer;
    }
}
