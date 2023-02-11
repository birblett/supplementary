package com.birblett.registry;

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
            .trackRangeBlocks(4).trackedUpdateRate(10)
            .build();

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "snowball_variant"), SNOWBALL_VARIANT);
        EntityRendererRegistry.register(SNOWBALL_VARIANT, (EntityRendererFactory<Entity>) context -> new FlyingItemEntityRenderer(context));
    }
}
