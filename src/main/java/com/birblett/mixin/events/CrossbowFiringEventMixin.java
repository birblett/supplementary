package com.birblett.mixin.events;

import com.birblett.Supplementary;
import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.registry.SupplementaryComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(CrossbowItem.class)
public class CrossbowFiringEventMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onUseEvent(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack) {
        EnchantmentHelper.get(itemStack).forEach((enchantment, lvl) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.getComponentType() == SupplementaryComponents.ComponentType.ARROW) {
                    List<ItemStack> projectiles = CrossbowItem.getProjectiles(itemStack);
                    if (!projectiles.isEmpty() && enchantmentBuilder.getComponent().maybeGet(user).isPresent()) {
                        enchantmentBuilder.getComponent().get(user).onCrossbowUse(itemStack, hand, projectiles.get(0));
                    }
                }
            }
        });
    }

    @Inject(method = "createArrow", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void arrowCreationEvent(World world, LivingEntity user, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        EnchantmentHelper.get(crossbow).forEach((enchantment, lvl) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.getComponentType() == SupplementaryComponents.ComponentType.ARROW) {
                    enchantmentBuilder.onProjectileFire(user, persistentProjectileEntity, lvl);
                }
            }
        });
    }
}
