package com.birblett.mixin.enchantments.assault_dash;

import com.birblett.registry.SupplementaryComponents;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Initiate a dash for Assault Dash
 */
@Mixin(ShieldItem.class)
public class AssaultDashShieldItemMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void applyAssaultDashTicks(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack;
        int level;
        if ((level = EnchantmentHelper.getLevel(SupplementaryEnchantments.ASSAULT_DASH, (stack = user.getStackInHand(hand)))) > 0) {
            int cooldown = EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, stack) > 0 ? 30 + level * 4 :
                    40 + level * 5;
            user.getItemCooldownManager().set(stack.getItem(), cooldown);
            if (user instanceof ServerPlayerEntity && stack.getMaxDamage() - stack.getDamage() > 1) {
                EquipmentSlot slot = user.getMainHandStack() == stack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.damage(1, user, e -> e.sendEquipmentBreakStatus(slot));
            }
            SupplementaryComponents.ASSAULT_DASH.get(user).setValue(level, user);
        }
    }
}
