package com.birblett.mixin.enchantments.growth;

import com.birblett.lib.helper.EnchantHelper;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies Growth draw speed modifiers to tridents
 */
@Mixin(TridentItem.class)
public class GrowthTridentItemMixin {

    @Unique private ItemStack supplementary$TridentStack;
    @Unique private LivingEntity supplementary$Holder;

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/TridentItem;getMaxUseTime(Lnet/minecraft/item/ItemStack;)I"))
    private void getTridentStack(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        this.supplementary$TridentStack = stack;
        this.supplementary$Holder = user;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "onStoppedUsing", at = @At(value = "STORE", ordinal = 0), index = 6)
    private int setUseTimeRemaining(int i) {
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.GROWTH, this.supplementary$TridentStack) > 0) {
            i = (int) EnchantHelper.getDrawSpeedModifier(this.supplementary$Holder, i, this.supplementary$TridentStack);
        }
        return i;
    }
}
