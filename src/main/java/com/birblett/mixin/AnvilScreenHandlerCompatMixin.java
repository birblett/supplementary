package com.birblett.mixin;

import com.birblett.lib.creational.EnchantmentBuilder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

/**
 * Allows anvil to use {@link EnchantmentBuilder#getCustomAnvilCost()} to determine custom anvil cost for repair.
 */
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerCompatMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I",
            ordinal = 1), index = 2)
    private int modifyCostMultiplier(int cost, @Local Enchantment enchantment, @Local(ordinal = 4) int lvl, @Local(ordinal = 5) int pastCost) {
        if (enchantment instanceof EnchantmentBuilder e && e.getCustomAnvilCost() >= 0) {
            cost -= lvl * pastCost;
            cost += e.getCustomAnvilCost() * pastCost;
        }
        return cost;
    }

}
