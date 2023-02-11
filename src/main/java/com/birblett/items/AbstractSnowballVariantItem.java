package com.birblett.items;

import com.birblett.entities.SnowballVariantEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public abstract class AbstractSnowballVariantItem extends Item {

    public AbstractSnowballVariantItem(Settings settings) {
        super(settings);
    }

    public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {}

    public void onBlockHitEvent(BlockHitResult blockHitResult, SnowballVariantEntity snowballVariantEntity) {}

    public boolean persistsAfterCollision() {
        return false;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F);
        if (!world.isClient) {
            SnowballVariantEntity slowballEntity = new SnowballVariantEntity(world, user);
            slowballEntity.setItem(itemStack);
            slowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            world.spawnEntity(slowballEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1); // decrements itemStack if user is not in creative mode
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
