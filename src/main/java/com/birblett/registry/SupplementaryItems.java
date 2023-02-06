package com.birblett.registry;



import com.birblett.trinkets.CapeItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.birblett.armor_materials.steel_plate.SteelPlateArmorMaterial;
import net.minecraft.item.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.birblett.Supplementary.MODID;

public class SupplementaryItems {

    // steel armor set
    public static final ArmorMaterial STEEL_PLATE_ARMOR_MATERIAL = new SteelPlateArmorMaterial();
    public static final Item STEEL_HELMET = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.HEAD,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_CHESTPLATE = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.CHEST,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_LEGGINGS = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.LEGS,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item STEEL_BOOTS = new ArmorItem(STEEL_PLATE_ARMOR_MATERIAL, EquipmentSlot.FEET,
            new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));

    public static final Item CAPE = new CapeItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "cape"), CAPE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_helmet"), STEEL_HELMET);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_chestplate"), STEEL_CHESTPLATE);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_leggings"), STEEL_LEGGINGS);
        Registry.register(Registry.ITEM, new Identifier(MODID, "steel_boots"), STEEL_BOOTS);
    }


}
