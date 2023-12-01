package com.birblett.mixin.cape;

import com.birblett.registry.SupplementaryItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Hacky workaround to allow for LoomScreenHandler to accept Capes on shift click
 */
@Mixin(LoomScreenHandler.class)
public class LoomScreenHandlerMixin {

    @Unique private ItemStack supplementary$CapeStack;
    @Unique private boolean supplementary$UseCustomLogic;

    @Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void isInput(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot) {
        supplementary$UseCustomLogic = index <= 40 && index >= 4;
    }

    @ModifyVariable(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.AFTER, by = 1), index = 5)
    private ItemStack setStackTemp(ItemStack stack){
        this.supplementary$CapeStack = ItemStack.EMPTY;
        if (stack.isOf(SupplementaryItems.CAPE) && supplementary$UseCustomLogic) {
            this.supplementary$CapeStack = stack;
            return new ItemStack(Items.WHITE_BANNER);
        }
        return stack;
    }

    @ModifyArg(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/LoomScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z",
            ordinal = 2))
    private ItemStack setBannerSlotStack(ItemStack stack) {
        if (!this.supplementary$CapeStack.isEmpty()) {
            return this.supplementary$CapeStack;
        }
        return stack;
    }

    @ModifyVariable(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V"),
            index = 5)
    private ItemStack replaceOldStack(ItemStack stack){
        if (!this.supplementary$CapeStack.isEmpty()) {
            return this.supplementary$CapeStack;
        }
        return stack;
    }
}
