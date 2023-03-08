package com.birblett.mixin.enchantments.enhanced;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Allows Channeling tridents with Enhanced to summon lightning anywhere
 */
@Mixin(TridentEntity.class)
public class EnhancedTridentEntityMixin {

    @Inject(method = "onEntityHit", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void channelingAlways(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f, Entity entity2, DamageSource damageSource, SoundEvent soundEvent, float g) {
        TridentEntity self = (TridentEntity) (Object) this;
        BlockPos blockPos = entity.getBlockPos();
        // Summon a lightning bolt if Enhanced and Channeling condition is otherwise false
        if (self.world instanceof ServerWorld && (!self.world.isThundering() || !self.world.isSkyVisible(blockPos)) &&
                self.hasChanneling() && EnchantmentHelper.getLevel(SupplementaryEnchantments.ENHANCED, self.tridentStack) > 0) {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(self.world);
            if (lightningEntity != null) {
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity2 : null);
            }
            self.world.spawnEntity(lightningEntity);
            soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
            g = 5.0F;
            self.playSound(soundEvent, g, 1.0F);
        }
    }
}
