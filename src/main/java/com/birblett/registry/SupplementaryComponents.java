package com.birblett.registry;

import com.birblett.lib.components.IntComponent;
import com.birblett.lib.components.LevelComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;

import static com.birblett.Supplementary.MODID;

public class SupplementaryComponents implements EntityComponentInitializer {

    public static enum ComponentType {
        NONE,
        ARROW,
        ENTITY,
        BLOCK_ENTITY
    }

    public static final ComponentKey<IntComponent> LIGHTNING_BOLT =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "lightning_bolt"), IntComponent.class);
    public static final ComponentKey<IntComponent> MARKED_LEVEL =
            ComponentRegistry.getOrCreate(new Identifier(MODID, "marked"), IntComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PersistentProjectileEntity.class, LIGHTNING_BOLT, e -> new LevelComponent("lightning_bolt"));
        registry.registerFor(PersistentProjectileEntity.class, MARKED_LEVEL, e -> new LevelComponent("marked"));
    }
}
