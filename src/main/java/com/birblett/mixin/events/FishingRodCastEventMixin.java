package com.birblett.mixin.events;

import com.birblett.api.ItemEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public class FishingRodCastEventMixin {

    @Unique private PlayerEntity supplementary$FishingRodUser;
    @Unique private ItemStack supplementary$FishingRodItemStack;

    @Inject(method = "use", at = @At("HEAD"))
    private void setItemStack(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        supplementary$FishingRodUser = user;
        supplementary$FishingRodItemStack = user.getStackInHand(hand);
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private Entity onCastEvent(Entity entity) {
        FishingBobberEntity fishingBobberEntity = (FishingBobberEntity) entity;
        ItemEvents.FISHING_ROD_USE.invoker().onProjectileFire(supplementary$FishingRodUser, fishingBobberEntity, supplementary$FishingRodItemStack);
        return entity;
    }
}
