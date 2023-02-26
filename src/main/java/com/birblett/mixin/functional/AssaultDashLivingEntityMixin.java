package com.birblett.mixin.functional;

import com.birblett.Supplementary;
import com.birblett.lib.helper.EntityHelper;
import com.birblett.lib.mixinterface.AssaultDashLivingEntityInterface;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public class AssaultDashLivingEntityMixin implements AssaultDashLivingEntityInterface {
    /*
    Implementation for shield dash+bash for Assault Dash
     */

    @Unique private int supplementary$AssaultDashTicks = 0;
    @Unique private Vec3d supplementary$AssaultDashVelocity;

    @Override
    public void setAssaultDash(int ticks, Vec3d velocity) {
        this.supplementary$AssaultDashTicks = ticks;
        this.supplementary$AssaultDashVelocity = velocity;
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void assaultDashingMovement(CallbackInfo ci) {
        if (this.supplementary$AssaultDashTicks > 0) {
            LivingEntity user = (LivingEntity) (Object) this;
            user.setVelocity(this.supplementary$AssaultDashVelocity);
            Item item = user.getActiveItem().getItem();
            if (item.getMaxUseTime(user.getActiveItem()) - user.getItemUseTimeLeft() >= 0 && user.isUsingItem() &&
                    item.getUseAction(user.getActiveItem()) == UseAction.BLOCK) {
                List<EntityHitResult> entityHitResults = EntityHelper.getEntityCollisions(user.world, user, user.getPos().subtract(this.supplementary$AssaultDashVelocity),
                        user.getPos().add(this.supplementary$AssaultDashVelocity), user.getBoundingBox().stretch(supplementary$AssaultDashVelocity).expand(1.0),
                        e -> true, 0.5f);
                for (EntityHitResult entityHitResult : entityHitResults) {
                    Entity target = entityHitResult.getEntity();
                    if (target.damage(SupplementaryEnchantments.shieldBash(user), (float) supplementary$AssaultDashVelocity.length() * 2)) {
                        target.setVelocity(target.getVelocity().add(this.supplementary$AssaultDashVelocity.multiply(1.2)).add(0, 0.2, 0));
                        if (target instanceof PlayerEntity) {
                            target.velocityModified = true;
                        }
                        if (user instanceof ServerPlayerEntity) {
                            if (!((ServerPlayerEntity) user).getAbilities().creativeMode) {
                                user.getActiveItem().damage(1, user.getRandom(), (ServerPlayerEntity) user);
                            }
                            user.world.playSoundFromEntity(null, target, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS,
                                    1.0f, user.world.random.nextFloat() * 0.4f);
                        }
                    }
                }
                if (this.supplementary$AssaultDashTicks < 8) {
                    this.supplementary$AssaultDashVelocity = this.supplementary$AssaultDashVelocity.multiply(0.75);
                }
                this.supplementary$AssaultDashTicks--;
            }
            else {
                this.supplementary$AssaultDashTicks = 0;
                user.setVelocity(user.getVelocity().multiply(0.3));
            }
        }
    }
}
