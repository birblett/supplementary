package com.birblett.mixin.enchantments.gluttony;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Handles exhaustion/hunger multiplier and saturation modifier for Gluttony enchantment
 */
@Mixin(PlayerEntity.class)
public class GluttonyPlayerEntityMixin {

    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float increaseExhaustionAmount(float exhaustion) {
        int l = EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.GLUTTONY, (PlayerEntity) (Object) this);
        if (l > 0) {
            exhaustion *= 1 + 0.3f * l;
        }
        return exhaustion;
    }

    @ModifyArgs(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"))
    private void overrideFoodEating(Args args) {
        Item item = args.get(0);
        int level = EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.GLUTTONY, (PlayerEntity) (Object) this);
        if (item.isFood() && level > 0) {
            args.set(0, Items.AIR);
            args.set(1, ItemStack.EMPTY);
            FoodComponent foodComponent = item.getFoodComponent();
            //noinspection ConstantConditions
            ((PlayerEntity) (Object) this).getHungerManager().add(foodComponent.getHunger(), foodComponent
                    .getSaturationModifier() * Math.min(0.1f, 1 - 0.3f * level));
        }
    }
}
