package com.birblett.mixin.events;

import com.birblett.lib.api.ItemEvents;
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

@Mixin(CrossbowItem.class)
public class CrossbowFiringEventMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void crossbowPrefireEvent(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack) {
        ItemEvents.CROSSBOW_PREFIRE.invoker().onItemUse(user, itemStack, hand);
    }

    @Inject(method = "createArrow", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void crossbowProjectileFireEvent(World world, LivingEntity user, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        ItemEvents.ARROW_PROJECTILE_FIRED.invoker().onProjectileFire(user, persistentProjectileEntity, crossbow);
    }
}
