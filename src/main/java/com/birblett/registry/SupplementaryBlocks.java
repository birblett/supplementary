package com.birblett.registry;

import com.birblett.Supplementary;
import com.birblett.blocks.MagicSand;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SupplementaryBlocks {

    // TODO: proper registration of item settings, block properties match stone atm
    public static final Block MAGIC_SAND = new MagicSand(FabricBlockSettings.of(Material.AGGREGATE).resistance(1200.0f).hardness(0.5f).requiresTool().sounds(BlockSoundGroup.SAND));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(Supplementary.MODID, "magic_sand"), MAGIC_SAND);
        Registry.register(Registry.ITEM, new Identifier(Supplementary.MODID, "magic_sand"), new BlockItem(MAGIC_SAND, new FabricItemSettings()));
    }
}
