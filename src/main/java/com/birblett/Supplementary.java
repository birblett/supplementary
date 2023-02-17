package com.birblett;

import com.birblett.registry.SupplementaryBlocks;
import com.birblett.registry.SupplementaryEnchantments;
import com.birblett.registry.SupplementaryEntities;
import com.birblett.registry.SupplementaryItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Supplementary implements ModInitializer {

	public static final Random SUPPLEMENTARY_RANDOM = new Random();
	public static final Logger LOGGER = LoggerFactory.getLogger("supplementary");
	public static final String MODID = "supplementary";

	@Override
	public void onInitialize() {
		SupplementaryBlocks.register();
		SupplementaryEnchantments.buildAndRegister();
		SupplementaryEntities.register();
		SupplementaryItems.register();
	}
}