package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Silk Touch and Fortune drops go directly to the player inventory with Enhanced
 */
@Mixin(Block.class)
public class EnhancedBlockMixin {

    @Unique private static PlayerEntity supplementary$MiningPlayer;

    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    private static void getPlayerEnhancedStatus(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player && (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0 ||
                EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack) > 0) && EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, stack) > 0) {
            supplementary$MiningPlayer = player;
        }
    }

    @ModifyArg(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private static Consumer<ItemStack> modifyOnDroppedEffect(Consumer<ItemStack> defaultConsumer) {
        if (supplementary$MiningPlayer != null) {
            return itemStack -> {
                supplementary$MiningPlayer.getInventory().insertStack(itemStack);
                if (itemStack.getCount() > 0) {
                    defaultConsumer.accept(itemStack);
                }
            };
        }
        return defaultConsumer;
    }
}
