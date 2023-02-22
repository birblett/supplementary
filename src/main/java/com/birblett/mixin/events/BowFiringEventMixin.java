package com.birblett.mixin.events;

import com.birblett.api.ItemEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public class BowFiringEventMixin {

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "net/minecraft/entity/projectile/PersistentProjectileEntity.setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void addEnchantmentsToArrowEntity(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        ItemEvents.ON_PROJECTILE_FIRED.invoker().onProjectileFire(user, persistentProjectileEntity, itemStack);
    }
}
