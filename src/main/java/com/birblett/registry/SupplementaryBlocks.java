package com.birblett.registry;

import com.birblett.Supplementary;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class SupplementaryBlocks {

    // TODO: texture path was "all": "supplementary:block/magic_sand", needs to be created
    public static final Block MAGIC_SAND = new Block(FabricBlockSettings.create().sounds(BlockSoundGroup.SAND).resistance(1200.0f).hardness(0.5f).requiresTool());

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(Supplementary.MODID, "magic_sand"), MAGIC_SAND);
        Registry.register(Registries.ITEM, new Identifier(Supplementary.MODID, "magic_sand"), new BlockItem(MAGIC_SAND, new FabricItemSettings()));
    }
}
