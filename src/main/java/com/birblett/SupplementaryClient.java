package com.birblett;

import com.birblett.registry.SupplementaryPacketRegistry;
import net.fabricmc.api.ClientModInitializer;

public class SupplementaryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SupplementaryPacketRegistry.registerClient();
    }
}
