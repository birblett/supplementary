package com.birblett.mixin.enchantments;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class SoulboundServerPlayerEntityMixin {
    /*
    When respawning a player after death, copies items over to the new player object, preserving armor slots
     */

    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void saveSoulboundItems(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (!alive) {
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            PlayerInventory inventory = oldPlayer.getInventory();
            for (int slot = 0; slot < inventory.size(); slot++) {
                if (EnchantmentHelper.getLevel(SupplementaryEnchantments.SOULBOUND, inventory.getStack(slot)) > 0) {
                    switch (slot) {
                        case 36, 37, 38, 39 -> self.getInventory().armor.set(slot - 36, inventory.getStack(slot));
                        case 40 -> self.getInventory().offHand.set(0, inventory.getStack(40));
                        default -> self.getInventory().insertStack(inventory.getStack(slot));
                    }
                }
            }
        }
    }
}
