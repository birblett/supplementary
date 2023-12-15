package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mildly scuffed way of adding Growth attack damage/speed values to item tooltips
 */
@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class GrowthItemStackMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyArg(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;", ordinal = 0))
    private double modifyTooltipValue(double val, @Local EntityAttributeModifier modifier) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, (ItemStack) (Object) this) > 0) {
            if (modifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
                return val + EnchantHelper.getGrowthStat((ItemStack) (Object) this, EnchantHelper.GrowthKey.ATTACK_DAMAGE);
            }
            else if (modifier.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
                return val + EnchantHelper.getGrowthStat((ItemStack) (Object) this, EnchantHelper.GrowthKey.ATTACK_SPEED);
            }
        }
        return val;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/TooltipContext;isAdvanced()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addBasicTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        // Adds a short tooltip if advanced tooltips not enabled
        if (!context.isAdvanced() && EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, (ItemStack) (Object) this) > 0) {
            list.add(MutableText.of(new TranslatableTextContent("item.supplementary.growth_total_tooltip", null,
                    new Object[]{(int) EnchantHelper.getTotalGrowthPoints((ItemStack) (Object) this), 1000})));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 16),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addAdvancedToolDip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        // Adds an in-depth tooltip if advanced tooltips are enabled
        ItemStack self = (ItemStack) (Object) this;
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, self) > 0) {
            list.add(MutableText.of(new TranslatableTextContent("item.supplementary.growth_total_tooltip", null,
                    new Object[]{(int) EnchantHelper.getTotalGrowthPoints(self), 1000})));
            for (Map.Entry<EnchantHelper.GrowthKey, Float> entry: EnchantHelper.getAllGrowthPoints(self).entrySet()) {
                float value = entry.getValue() * entry.getKey().scale * (entry.getKey().isPercentage ? 100 : 1);
                BigDecimal format = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
                if (value > 0) {
                    list.add(MutableText.of(new TranslatableTextContent("item.supplementary.growth_stat_tooltip", null,
                            new Object[]{entry.getKey().name, entry.getValue().intValue(), entry.getKey().prefix, format,
                                    entry.getKey().isPercentage ? "%" : ""})));
                }
            }
        }
    }
}
