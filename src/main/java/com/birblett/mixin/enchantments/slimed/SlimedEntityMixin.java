package com.birblett.mixin.enchantments.slimed;

import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class SlimedEntityMixin {
    /*
    Applies slimed bounciness to entities with the slimed enchant
     */

    @Unique private Block oldBlock;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "move", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"),
            index = 10)
    private Block replaceSlime(Block block) {
        this.oldBlock = block;
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0
                && !self.isTouchingWater() && self.isOnGround()) {
            block = Blocks.SLIME_BLOCK;
        }
        return block;
    }

    @ModifyVariable(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;bypassesSteppingEffects()Z"),
            index = 10)
    private Block replaceOldBlock(Block block) {
        return this.oldBlock;
    }

    @ModifyVariable(method = "playStepSound", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), index = 2, argsOnly = true)
    private BlockState replaceSlimedStepSound(BlockState blockState) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            blockState = Blocks.SLIME_BLOCK.getDefaultState();
        }
        return blockState;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "playStepSound", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), index = 3)
    private BlockState replaceSlimedInBlockStepSound(BlockState blockState) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            blockState = Blocks.SLIME_BLOCK.getDefaultState();
        }
        return blockState;
    }

    @ModifyArg(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private ParticleEffect replaceSlimedSprintParticles(ParticleEffect particleEffect) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState());
        }
        return particleEffect;
    }
}
