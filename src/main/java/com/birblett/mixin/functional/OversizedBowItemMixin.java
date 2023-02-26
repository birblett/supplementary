package com.birblett.mixin.functional;

import com.birblett.lib.helper.SupplementaryEnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class OversizedBowItemMixin {

    @Unique private static ItemStack supplementary$BowItemStack;

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void getOversizedLevel(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        supplementary$BowItemStack = stack;
    }

    @ModifyVariable(method = "getPullProgress", at = @At(value = "STORE", ordinal = 1), index = 1)
    private static float scalePullProgress(float pullProgress) {
        return SupplementaryEnchantmentHelper.getOversizedDrawspeedModifier(pullProgress, supplementary$BowItemStack);
    }
}
