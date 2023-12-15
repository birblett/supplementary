package com.birblett;

import com.birblett.registry.*;
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
		SupplementaryAttributes.register();
		SupplementaryBlocks.register();
		SupplementaryEnchantments.register();
		SupplementaryEntities.register();
		SupplementaryEvents.register();
		SupplementaryItems.register();
	}
}