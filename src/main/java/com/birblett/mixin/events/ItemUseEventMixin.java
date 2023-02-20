package com.birblett.mixin.events;

import com.birblett.lib.builders.EnchantmentBuilder;
import com.birblett.lib.components.BaseComponent;
import com.birblett.registry.SupplementaryComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemUseEventMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void onUseEvent(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        EnchantmentHelper.get(user.getStackInHand(hand)).forEach((enchantment, level) -> {
            if (enchantment instanceof EnchantmentBuilder enchantmentBuilder) {
                if (enchantmentBuilder.hasComponent()) {
                    for (ComponentKey<BaseComponent> componentKey : enchantmentBuilder.getComponents()) {
                        componentKey.maybeGet(user).ifPresent(component -> component.onUse(user, hand));
                    }
                }
                else {
                    enchantmentBuilder.onUse(user, hand);
                }
            }
        });
    }
}
