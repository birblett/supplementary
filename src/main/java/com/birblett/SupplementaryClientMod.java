package com.birblett;

import com.birblett.util.ModModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;

public class SupplementaryClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient(){


        ModModelPredicateProvider.registerModels();
    }

}
