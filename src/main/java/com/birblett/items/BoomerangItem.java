package com.birblett.items;

import com.birblett.entities.BoomerangEntity;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class BoomerangItem extends ToolItem implements SupplementaryEnchantable {

    private final List<Class<? extends Enchantment>> enchantments = List.of(PiercingEnchantment.class, DamageEnchantment.class);

    public BoomerangItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack activeStack = user.getStackInHand(hand);
        BoomerangEntity boomerangEntity = new BoomerangEntity(user, world, !user.isCreative());
        boomerangEntity.setStack(activeStack.copy());
        if (EnchantmentHelper.getLevel(SupplementaryEnchantments.PICKUP, activeStack) > 0) {
            boomerangEntity.setPickupLevel(EnchantmentHelper.getLevel(SupplementaryEnchantments.PICKUP, activeStack));
        }
        if (EnchantmentHelper.getLevel(Enchantments.PIERCING, activeStack) > 0) {
            boomerangEntity.setPierceLevel(EnchantmentHelper.getLevel(Enchantments.PIERCING, activeStack));
        }
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

    @Override
    public List<Class<? extends Enchantment>> getValidEnchantments() {
        return enchantments;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
