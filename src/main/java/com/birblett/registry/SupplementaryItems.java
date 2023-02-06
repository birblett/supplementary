package com.birblett.registry;

import com.birblett.trinkets.CapeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.birblett.Supplementary.MODID;

public class SupplementaryItems {

    //Steel armor set
    public static final Item STEEL_HELMET = new Item(new FabricItemSettings());

    public static final Item CAPE = new CapeItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "cape"), CAPE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_helmet"), STEEL_HELMET);
    }
}
