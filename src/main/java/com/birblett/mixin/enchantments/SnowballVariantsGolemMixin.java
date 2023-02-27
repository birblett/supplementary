package com.birblett.mixin.enchantments;

import com.birblett.entities.SnowballVariantEntity;
import com.birblett.registry.SupplementaryComponents;
import com.birblett.registry.SupplementaryItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemEntity.class)
public class SnowballVariantsGolemMixin {
    /*
    Replaces snow golem snowballs with variant snowballs
     */

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void replaceSnowballs(LivingEntity target, float pullProgress, CallbackInfo ci) {
        /*
        Replace snow golem snowball with custom projectiles if necessary
         */
        SnowGolemEntity self = (SnowGolemEntity) (Object) this;
        int snowballType = SupplementaryComponents.SNOWBALL_TYPE.get(self).getValue();
        if (snowballType > 0) {
            SnowballVariantEntity projectile = new SnowballVariantEntity(self.world, self);
            switch (snowballType) {
                case 1 -> projectile.setItem(new ItemStack(SupplementaryItems.GLOWBALL));
                case 2 -> projectile.setItem(new ItemStack(SupplementaryItems.ICEBALL));
                case 3 -> projectile.setItem(new ItemStack(SupplementaryItems.SLOWBALL));
                case 4 -> projectile.setItem(new ItemStack(SupplementaryItems.BLOWBALL));
                default -> {}
            }
            double d = target.getEyeY() - (double)1.1f;
            double e = target.getX() - self.getX();
            double f = d - projectile.getY();
            double g = target.getZ() - self.getZ();
            double h = Math.sqrt(e * e + g * g) * (double)0.2f;
            projectile.setVelocity(e, f + h, g, 1.6f, 12.0f);
            self.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 0.4f / (self.getRandom().nextFloat() * 0.4f + 0.8f));
            self.world.spawnEntity(projectile);
            ci.cancel();
        }
    }
}
