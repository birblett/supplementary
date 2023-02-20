package com.birblett.registry;

import com.birblett.client.render.entities.BoomerangEntityRenderer;
import com.birblett.entities.BoomerangEntity;
import com.birblett.entities.SnowballVariantEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.birblett.Supplementary.MODID;

public class SupplementaryEntities {

    public static final EntityType<SnowballVariantEntity> SNOWBALL_VARIANT = FabricEntityTypeBuilder.<SnowballVariantEntity>create(
            SpawnGroup.MISC, SnowballVariantEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeBlocks(32).trackedUpdateRate(20)
            .build();

    public static final EntityType<BoomerangEntity> BOOMERANG = FabricEntityTypeBuilder.<BoomerangEntity>create(
            SpawnGroup.MISC, com.birblett.entities.BoomerangEntity::new)
            .dimensions(EntityDimensions.fixed(0.4F, 0.1f))
            .trackRangeBlocks(128).trackedUpdateRate(20)
            .build();

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "snowball_variant"), SNOWBALL_VARIANT);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "boomerang"), BOOMERANG);
        EntityRendererRegistry.register(SNOWBALL_VARIANT, (EntityRendererFactory<Entity>) context -> new FlyingItemEntityRenderer(context));
        EntityRendererRegistry.register(BOOMERANG, BoomerangEntityRenderer::new);
    }
}
