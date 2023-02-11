package com.birblett.registry;



import com.birblett.entities.SnowballVariantEntity;
import com.birblett.items.AbstractSnowballVariantItem;
import com.birblett.trinkets.CapeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.birblett.armor_materials.steel_plate.SteelPlateArmorMaterial;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import static com.birblett.Supplementary.MODID;

public class SupplementaryItems {

    public static final Item CAPE = new CapeItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static final ArmorMaterial STEEL_PLATE_ARMOR_MATERIAL = new SteelPlateArmorMaterial();
    public static final Item STEEL_HELMET = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.HEAD,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_CHESTPLATE = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.CHEST,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_LEGGINGS = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.LEGS,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_BOOTS = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.FEET,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));

    public static final Item GLOWBALL = new AbstractSnowballVariantItem(new Item.Settings().maxCount(16)) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 50, 0, false, false));
            }
            float damageAmount = target instanceof BlazeEntity ? 4.0f : 1.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item ICEBALL = new AbstractSnowballVariantItem(new Item.Settings().maxCount(16)) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            float damageAmount = target instanceof BlazeEntity ? 4.0f : 2.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item SLOWBALL = new AbstractSnowballVariantItem(new Item.Settings().maxCount(16)) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 50, 1));
            }
            float damageAmount = target instanceof BlazeEntity ? 4.0f : 1.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item SNOWGOLEMBALL = new AbstractSnowballVariantItem(new Item.Settings().maxCount(16)) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            SnowGolemEntity snowGolemEntity = new SnowGolemEntity(EntityType.SNOW_GOLEM, snowballVariantEntity.getWorld());
            snowGolemEntity.setPosition(snowballVariantEntity.getPos());
            if (target instanceof LivingEntity livingEntity) {
                snowGolemEntity.setTarget(livingEntity);
            }
            snowballVariantEntity.getWorld().spawnEntity(snowGolemEntity);
        }

        @Override
        public void onBlockHitEvent(BlockHitResult blockHitResult, SnowballVariantEntity snowballVariantEntity) {
            SnowGolemEntity snowGolemEntity = new SnowGolemEntity(EntityType.SNOW_GOLEM, snowballVariantEntity.getWorld());
            Vec3d blockPos2Vec = Vec3d.of(blockHitResult.getBlockPos().add(blockHitResult.getSide().getVector()));
            snowGolemEntity.setPosition(blockPos2Vec.add(0.5, 0.0, 0.5));
            snowballVariantEntity.getWorld().spawnEntity(snowGolemEntity);
        }
    };

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "cape"), CAPE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_helmet"), STEEL_HELMET);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_chestplate"), STEEL_CHESTPLATE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_leggings"), STEEL_LEGGINGS);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_boots"), STEEL_BOOTS);

        Registry.register(Registry.ITEM, new Identifier(MODID, "glowball"), GLOWBALL);
        Registry.register(Registry.ITEM, new Identifier(MODID, "iceball"), ICEBALL);
        Registry.register(Registry.ITEM, new Identifier(MODID, "slowball"), SLOWBALL);
        Registry.register(Registry.ITEM, new Identifier(MODID, "snowgolemball"), SNOWGOLEMBALL);
    }
}
