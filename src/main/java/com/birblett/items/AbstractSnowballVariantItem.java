package com.birblett.items;

import com.birblett.entities.SnowballVariantEntity;
import com.birblett.registry.SupplementaryComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public abstract class AbstractSnowballVariantItem extends Item {

    private final int id;

    public AbstractSnowballVariantItem(Settings settings, int id) {
        super(settings);
        this.id = id;
    }

    public AbstractSnowballVariantItem(Settings settings) {
        this(settings, 0);
    }

    public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {}

    public void onBlockHitEvent(BlockHitResult blockHitResult, SnowballVariantEntity snowballVariantEntity) {}

    public boolean persistsAfterCollision() {
        return false;
    }

    public ActionResult useOnEntity(ItemStack itemStack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity) {
            if (SupplementaryComponents.SNOWBALL_TYPE.get(entity).getValue() != this.id) {
                SupplementaryComponents.SNOWBALL_TYPE.get(entity).setValue(this.id);
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                entity.playSound(SoundEvents.BLOCK_SNOW_PLACE, 1.0f, 0.4f / (entity.getRandom().nextFloat() * 0.4f + 0.8f));
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
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
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
