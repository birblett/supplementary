package com.birblett.mixin.events;

import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.registry.SupplementaryComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class CrossbowFiringEventMixin {

    @Inject(method = "createArrow(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;",
            at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
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
