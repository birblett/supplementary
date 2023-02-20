package com.birblett.mixin.functional;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerInventory.class)
public class SoulboundPlayerInventoryMixin {
    /*
    Preserves items enchanted with Soulbound instead of removing them, which are copied to the respawned player in SouldboundServerPlayerEntityImpl
     */

    @Unique ItemStack savedStack;

    @ModifyArg(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    private ItemStack excludeSoulboundFromDropPool(ItemStack stack) {
        this.savedStack = ItemStack.EMPTY;
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.SOULBOUND, stack) > 0) {
            this.savedStack = stack;
        }
        return this.savedStack != ItemStack.EMPTY ? ItemStack.EMPTY : stack;
    }

    @ModifyArg(method = "dropAll", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object keepSoulboundItems(Object stack) {
        return this.savedStack;
    }
}
