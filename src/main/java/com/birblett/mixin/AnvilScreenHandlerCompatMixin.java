package com.birblett.mixin;

import com.birblett.Supplementary;
import com.birblett.lib.creational.EnchantmentBuilder;
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

    @Unique private Enchantment supplementary$PastEnchantment;
    @Unique private int supplementary$PastEnchLevel;
    @Unique private int supplementary$PastCost;

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getEnchantment(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3,
                                Map map, boolean bl, Map map2, boolean bl2, boolean bl3, Iterator var12, Enchantment enchantment,
                                int q, int r, boolean bl4, int s) {
        this.supplementary$PastEnchantment = enchantment;
        this.supplementary$PastEnchLevel = r;
        this.supplementary$PastCost = r * s;
    }

    @ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I",
            ordinal = 1), index = 2)
    private int modifyCostMultiplier(int cost) {
        if (this.supplementary$PastEnchantment instanceof EnchantmentBuilder e && e.getCustomAnvilCost() >= 0) {
            cost -= this.supplementary$PastCost;
            cost += e.getCustomAnvilCost() * this.supplementary$PastEnchLevel;
        }
        return cost;
    }

}
