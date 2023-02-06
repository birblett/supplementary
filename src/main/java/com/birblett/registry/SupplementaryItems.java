package com.birblett.registry;

import com.birblett.trinkets.CapeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.birblett.armor_materials.steel_plate.SteelPlateArmorMaterial;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.birblett.Supplementary.MODID;

public class SupplementaryItems {

    // steel armor set
    public static final ArmorMaterial STEEL_PLATE_ARMOR_MATERIAL = new SteelPlateArmorMaterial();
    public static final Item STEEL_HELMET = new Item(new FabricItemSettings());
    public static final Item STEEL_CHESTPLATE = new Item(new FabricItemSettings());
    public static final Item STEEL_LEGGINGS = new Item(new FabricItemSettings());
    public static final Item STEEL_BOOTS = new Item(new FabricItemSettings());

    public static final Item CAPE = new CapeItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "cape"), CAPE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_helmet"), STEEL_HELMET);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_helmet"), STEEL_HELMET);
    }
}
