package com.birblett;

import com.birblett.registry.SupplementaryEnchantments;
import com.birblett.registry.SupplementaryItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Supplementary implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("supplementary");
	public static final String MODID = "supplementary";

	@Override
	public void onInitialize() {
		SupplementaryEnchantments.register();
		SupplementaryItems.register();
	}
}