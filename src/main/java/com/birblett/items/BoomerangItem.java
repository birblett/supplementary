package com.birblett.items;

import com.birblett.entities.BoomerangEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
        BoomerangEntity boomerangEntity = new BoomerangEntity(user, world, !user.isCreative());
        boomerangEntity.setStack(activeStack.copy());
        // -99: magic number for offhand slot
        boomerangEntity.setStoredSlot(hand == Hand.OFF_HAND ? -99 : user.getInventory().getSlotWithStack(activeStack));
        boomerangEntity.setYaw(user.getHeadYaw());
        boomerangEntity.setPitch(user.getPitch());
        boomerangEntity.setPosition(user.getEyePos());
        boomerangEntity.setVelocity(user.getRotationVector().normalize().multiply(1.6));
        world.spawnEntity(boomerangEntity);
        user.getItemCooldownManager().set(activeStack.getItem(), 8);
        if (!user.isCreative()) {
            user.getInventory().removeOne(activeStack);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS,
                0.5F, 0.3F);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
