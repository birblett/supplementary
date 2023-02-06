package com.birblett.registry;

import com.birblett.lib.builders.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

public class SupplementaryEnchantments {

    public static final EquipmentSlot[] BOTH_HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    public static final EnchantmentBuilder BURST_FIRE = new EnchantmentBuilder("burst_fire", Enchantment.Rarity.COMMON,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS);
    public static final EnchantmentBuilder LIGHTNING_BOLT = new EnchantmentBuilder("lightning_bolt", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.BOW, BOTH_HANDS);
    public static final EnchantmentBuilder MARKED = new EnchantmentBuilder("marked", Enchantment.Rarity.VERY_RARE,
            EnchantmentTarget.CROSSBOW, BOTH_HANDS) {
        @Override
        public boolean onProjectileFire(LivingEntity user, PersistentProjectileEntity projectileEntity, int level) {
            this.getComponent().get(projectileEntity).setValue(level);
            LivingEntity target = SupplementaryComponents.MARKED_TRACKED_ENTITY.get(user).getEntity();
            SupplementaryComponents.MARKED_TRACKED_ENTITY.get(projectileEntity).setEntity(target);
            return false;
        }

        @Override
        public void onDamage(LivingEntity target, PersistentProjectileEntity projectileEntity, int level) {
            if (projectileEntity.getOwner() instanceof LivingEntity livingEntity) {
                SupplementaryComponents.MARKED_TRACKED_ENTITY.get(livingEntity).setEntity(target);
            }
        }
    };

    public static void buildEnchantments() {
        BURST_FIRE.makeIncompatible(Enchantments.MULTISHOT)
                .setPower(20, 50);
        LIGHTNING_BOLT.makeIncompatible(Enchantments.POWER)
                .setPower(20, 50);
        MARKED.makeIncompatible(BURST_FIRE)
                .setPower(20, 5, 25, 5)
                .setMaxLevel(3)
                .addComponent(SupplementaryComponents.MARKED_LEVEL, SupplementaryComponents.ComponentType.ARROW);

    }

    public static void register() {
        buildEnchantments();
        BURST_FIRE.register();
        LIGHTNING_BOLT.register();
        MARKED.register();
    }
}
