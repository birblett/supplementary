package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public class SyncedEnchantmentComponent extends EnchantmentComponent implements AutoSyncedComponent {

    public SyncedEnchantmentComponent(String id) {
        super(id);
    }
}
