package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import com.birblett.registry.SupplementaryEnchantments;
import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique private int supplementary$toolTipAttributeModifier;

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getValue()D"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getModifier(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List list, MutableText mutableText, int i, EquipmentSlot[] var6, int var7, int var8, EquipmentSlot equipmentSlot, Multimap multimap, Iterator var11, Map.Entry entry, EntityAttributeModifier entityAttributeModifier) {
        this.supplementary$toolTipAttributeModifier = 0;
        if (entityAttributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
            this.supplementary$toolTipAttributeModifier = 1;
        }
        if (entityAttributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
            this.supplementary$toolTipAttributeModifier = 2;
        }
    }

    @ModifyArg(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;", ordinal = 0))
    private double modifyTooltipValue(double val) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, (ItemStack) (Object) this) > 0) {
            if (this.supplementary$toolTipAttributeModifier == 1) {
                return val + SupplementaryEnchantmentHelper.getGrowthStat((ItemStack) (Object) this, SupplementaryEnchantmentHelper.GrowthKey.ATTACK_DAMAGE);
            }
            else if (this.supplementary$toolTipAttributeModifier == 2) {
                return val + SupplementaryEnchantmentHelper.getGrowthStat((ItemStack) (Object) this, SupplementaryEnchantmentHelper.GrowthKey.ATTACK_SPEED);
            }
        }
        return val;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/TooltipContext;isAdvanced()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addBasicTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        // Adds a short tooltip if advanced tooltips not enabled
        if (!context.isAdvanced() && EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, (ItemStack) (Object) this) > 0) {
            list.add(new TranslatableText("item.supplementary.growth_total_tooltip", (int) SupplementaryEnchantmentHelper.getTotalGrowthPoints((ItemStack) (Object) this),
                    1000));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 16),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addAdvancedToolDip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        // Adds an in-depth tooltip if advanced tooltips are enabled
        ItemStack self = (ItemStack) (Object) this;
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, self) > 0) {
            list.add(new TranslatableText("item.supplementary.growth_total_tooltip", (int) SupplementaryEnchantmentHelper.getTotalGrowthPoints(self),
                    1000));
            for (Map.Entry<SupplementaryEnchantmentHelper.GrowthKey, Float> entry: SupplementaryEnchantmentHelper.getAllGrowthPoints(self).entrySet()) {
                float value = entry.getValue() * entry.getKey().scale * (entry.getKey().isPercentage ? 100 : 1);
                BigDecimal format = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
                if (value > 0) {
                    list.add(new TranslatableText("item.supplementary.growth_stat_tooltip", entry.getKey().name, entry.getValue().intValue(),
                            entry.getKey().prefix, format, entry.getKey().isPercentage ? "%" : ""));
                }
            }
        }
    }
}
