package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Aqua affinity also improves mining speed while in the air with Enhanced
 */
@Mixin(PlayerEntity.class)
public class EnhancedPlayerEntityMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void improveAirBlockBreakSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity user = ((PlayerEntity) (Object) this);
        if (!user.isOnGround()) {
            for (ItemStack itemStack : user.getArmorItems()) {
                if (EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, itemStack) > 0 &&EnchantmentHelper.getLevel(Enchantments.AQUA_AFFINITY, itemStack) > 0 )
                    cir.setReturnValue(cir.getReturnValue() * 5.0f);
                break;
            }
        }
    }
}
