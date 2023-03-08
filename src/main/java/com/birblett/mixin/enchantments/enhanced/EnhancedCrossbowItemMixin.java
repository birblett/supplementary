package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

/**
 * Multishot fires 5 arrows instead of 3 if Enhanced is present
 */
@Mixin(CrossbowItem.class)
public class EnhancedCrossbowItemMixin {

    @Inject(method = "shootAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V",
            ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void enhancedShoot(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo ci, List<ItemStack> list, float[] fs, int i, ItemStack itemStack, boolean bl) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, stack) > 0) {
            CrossbowItem.shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -5.0f);
            CrossbowItem.shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 5.0f);
        }
    }
}
