package com.birblett.items;

import com.birblett.Supplementary;
import com.birblett.entities.BoomerangEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BoomerangItem extends ToolItem {

    public BoomerangItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack activeStack = user.getStackInHand(hand);
        //user.getInventory().getSlotWithStack(activeStack);
        BoomerangEntity boomerangEntity = new BoomerangEntity(user, world, !user.isCreative());
        boomerangEntity.setStack(activeStack.copy());
        boomerangEntity.setYaw(user.getYaw());
        boomerangEntity.setPitch(user.getPitch());
        boomerangEntity.setPosition(user.getEyePos());
        boomerangEntity.setVelocity(user.getRotationVector().normalize().multiply(1.6));
        world.spawnEntity(boomerangEntity);
        user.getItemCooldownManager().set(activeStack.getItem(), 8);
        if (!user.isCreative()) {
            user.getInventory().removeOne(activeStack);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
