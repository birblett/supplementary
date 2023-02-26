package com.birblett.mixin.functional;

import com.birblett.lib.mixinterface.AssaultDashLivingEntityInterface;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
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

@Mixin(ShieldItem.class)
public class AssaultDashShieldItemMixin {
    /*
    Initiate an assault dash with initial velocity dependent on level
     */

    @Inject(method = "use", at = @At("HEAD"))
    private void applyAssaultDashTicks(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack;
        int level;
        if ((level = EnchantmentHelper.getLevel(SupplementaryEnchantments.ASSAULT_DASH, (stack = user.getStackInHand(hand)))) > 0) {
            user.getItemCooldownManager().set(stack.getItem(), 40 + level * 5);
            if (user instanceof ServerPlayerEntity) {
                stack.damage(1, user.getRandom(), (ServerPlayerEntity) user);
            }
            ((AssaultDashLivingEntityInterface) user).setAssaultDash(10, user.getRotationVecClient().multiply(1, 0, 1).normalize().multiply(0.6 + level * 0.3));
        }
    }
}
