package com.birblett.mixin.events;

import com.birblett.lib.api.EntityEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackEventMixin {

    @Inject(method = "postMine", at = @At("HEAD"))
    private void onSuccessfulMineEvent(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo ci) {
        EntityEvents.POSTMINE_EVENT.invoker().onBlockBreakEvent(world, state, pos, miner, (ItemStack) (Object) this);
    }
}
