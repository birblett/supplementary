package com.birblett.mixin.enchantments.slimed;

import com.birblett.Supplementary;
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
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Tricks game into thinking all solid blocks are slime blocks with Slimed enchantment equipped
 */
@Mixin(Entity.class)
public class SlimedEntityMixin {

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

    @ModifyVariable(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;)V"),
            index = 10)
    private Block replaceOldBlock(Block block) {
        return this.oldBlock;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "playCombinationStepSounds", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"),
            index = 3)
    private BlockSoundGroup replaceSlimedCombinationSounds(BlockSoundGroup soundGroup) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            soundGroup = Blocks.SLIME_BLOCK.getSoundGroup(Blocks.SLIME_BLOCK.getDefaultState());
        }
        return soundGroup;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "playStepSound", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"),
            index = 3)
    private BlockSoundGroup replaceSlimedStepSound(BlockSoundGroup soundGroup) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            soundGroup = Blocks.SLIME_BLOCK.getSoundGroup(Blocks.SLIME_BLOCK.getDefaultState());
        }
        return soundGroup;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "playSecondaryStepSound", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"), index = 2)
    private BlockSoundGroup replaceSlimedSecondaryStepSound(BlockSoundGroup soundGroup) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            soundGroup = Blocks.SLIME_BLOCK.getSoundGroup(Blocks.SLIME_BLOCK.getDefaultState());
        }
        return soundGroup;
    }

    @ModifyArg(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private ParticleEffect replaceSlimedSprintParticles(ParticleEffect particleEffect) {
        if ((Entity) (Object) this instanceof LivingEntity self && EnchantmentHelper.getEquipmentLevel(SupplementaryEnchantments.SLIMED, self) > 0) {
            particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState());
        }
        return particleEffect;
    }
}
