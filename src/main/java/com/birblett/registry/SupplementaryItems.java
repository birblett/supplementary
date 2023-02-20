package com.birblett.registry;



import com.birblett.entities.SnowballVariantEntity;
import com.birblett.items.BoomerangItem;
import com.birblett.items.SnowballVariantItem;
import com.birblett.trinkets.CapeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static com.birblett.Supplementary.MODID;

public class SupplementaryItems {
    /*
    This class contains all item and material declarations with associated data, and provides helper methods to register
    specific types of items.

    Trinkets
        CAPE - CapeItem, registered under id supplementary:cape
            Implementation - com.birblett.trinkets.CapeItem
            Renderer - com.birblett.client.render.items.CapeFeatureRenderer, instantiated in com.birblett.mixin.render.PlayerCapeRender
    Snowball variants
        SNOWGOLEMBALL - Registered under id supplementary:snowgolemball, cannot replace normal snow golem projectiles
        GLOWBALL - Registered under id supplementary:glowball
        ICEBALL - Registered under id supplementary:snowgolemball
        SLOWBALL - Registered under id supplementary:snowgolemball
        BLOWBALL - Registered under id supplementary:snowgolemball
    Boomerangs
        WOODEN_BOOMERANG - registered under id supplementary:wooden_boomerang
        STONE_BOOMERANG - registered under id supplementary:stone_boomerang
        IRON_BOOMERANG - registered under id supplementary:iron_boomerang
        GOLD_BOOMERANG - registered under id supplementary:gold_boomerang
        DIAMOND_BOOMERANG - registered under id supplementary:diamond_boomerang
        NETHERITE_BOOMERANG - registered under id supplementary:netherite_boomerang

    Methods:
        registerItem(String, Item) - registers the item under the provided id
        registerSnowballVariant(String, Item) - registers item + registers snowball dispenser behavior for the item
     */

    public static final Item CAPE = new CapeItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static final Item SNOWGOLEMBALL = new SnowballVariantItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC)) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            /*
            spawn snow golem upon hitting a target; if target is a LivingEntity, snow golem will aggro on it.
             */
            SnowGolemEntity snowGolemEntity = new SnowGolemEntity(EntityType.SNOW_GOLEM, snowballVariantEntity.getWorld());
            snowGolemEntity.setPosition(snowballVariantEntity.getPos());
            if (target instanceof LivingEntity livingEntity) {
                snowGolemEntity.setTarget(livingEntity);
            }
            snowballVariantEntity.getWorld().spawnEntity(snowGolemEntity);
        }

        @Override
        public void onBlockHitEvent(BlockHitResult blockHitResult, SnowballVariantEntity snowballVariantEntity) {
            /*
            spawn a snow golem on block hit based on the block face, in the center of the adjacent block
             */
            SnowGolemEntity snowGolemEntity = new SnowGolemEntity(EntityType.SNOW_GOLEM, snowballVariantEntity.getWorld());
            Vec3d blockPos2Vec = Vec3d.of(blockHitResult.getBlockPos().add(blockHitResult.getSide().getVector()));
            // Add 0.5 to x/z as BlockPos -> vector returns the corner of the block
            snowGolemEntity.setPosition(blockPos2Vec.add(0.5, 0.0, 0.5));
            snowballVariantEntity.getWorld().spawnEntity(snowGolemEntity);
        }
    };
    public static final Item GLOWBALL = new SnowballVariantItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC), 1) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            /*
            applies glowing (2.5s) on hit, and deals 1 damage, or 4 if target is a Blaze
             */
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 50, 0, false, false));
            }
            float damageAmount = target instanceof BlazeEntity ? 4.0f : 1.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item ICEBALL = new SnowballVariantItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC), 2) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            /*
            deals 2 damage, or 7 if target is a blaze or ender dragon
             */
            float damageAmount = target instanceof BlazeEntity || target instanceof EnderDragonPart ? 7.0f : 2.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item SLOWBALL = new SnowballVariantItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC), 3) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            /*
            applies slowness (2s) on hit, and deals 1 damage, or 4 if target is a Blaze
             */
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));
            }
            float damageAmount = target instanceof BlazeEntity ? 4.0f : 1.0f;
            target.damage(DamageSource.thrownProjectile(snowballVariantEntity, snowballVariantEntity.getOwner()), damageAmount);
        }
    };
    public static final Item BLOWBALL = new SnowballVariantItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC), 4) {
        @Override
        public void onEntityHitEvent(Entity target, SnowballVariantEntity snowballVariantEntity) {
            /*
            applies directional velocity modification of (0.5, 0.1, 0.0), scaled down by the target's knockback resistance
             */
            double knockbackScale = 1.0;
            if (target instanceof LivingEntity livingEntity && livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) > 0) {
                knockbackScale -= livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            }
            target.setVelocity(target.getVelocity().add(snowballVariantEntity.getVelocity().normalize()
                    .multiply(0.5 * (knockbackScale < 0 ? 0 : knockbackScale)).add(0.0, 0.1 * (knockbackScale < 0 ? 0 : knockbackScale), 0.0)));
            target.velocityModified = true;
        }
    };

    public static final Item WOODEN_BOOMERANG = new BoomerangItem(ToolMaterials.WOOD,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STONE_BOOMERANG = new BoomerangItem(ToolMaterials.STONE,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item IRON_BOOMERANG = new BoomerangItem(ToolMaterials.IRON,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item GOLD_BOOMERANG = new BoomerangItem(ToolMaterials.GOLD,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item DIAMOND_BOOMERANG = new BoomerangItem(ToolMaterials.DIAMOND,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item NETHERITE_BOOMERANG = new BoomerangItem(ToolMaterials.NETHERITE,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));

    private static void registerItem(String id, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, id), item);
    }

    private static void registerSnowballVariant(String id, Item item) {
        DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior(){
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new SnowballVariantEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
            }
        });
        registerItem(id, item);
    }

    public static void register() {
        registerItem("wooden_boomerang", WOODEN_BOOMERANG);
        registerItem("stone_boomerang", STONE_BOOMERANG);
        registerItem("iron_boomerang", IRON_BOOMERANG);
        registerItem("gold_boomerang", GOLD_BOOMERANG);
        registerItem("diamond_boomerang", DIAMOND_BOOMERANG);
        registerItem("netherite_boomerang", NETHERITE_BOOMERANG);

        registerItem("cape", CAPE);

        registerSnowballVariant("blowball", BLOWBALL);
        registerSnowballVariant("glowball", GLOWBALL);
        registerSnowballVariant("iceball", ICEBALL);
        registerSnowballVariant("slowball", SLOWBALL);
        registerSnowballVariant("snowgolemball", SNOWGOLEMBALL);
    }
}
