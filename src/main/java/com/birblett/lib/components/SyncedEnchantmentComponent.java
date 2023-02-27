package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

/**
 * An EnchantmentComponent subclass synced via the {@link ComponentKey#sync(Object)}
 * method
 */
public class SyncedEnchantmentComponent extends EnchantmentComponent implements AutoSyncedComponent {

    /**
     * See: {@link EnchantmentComponent#EnchantmentComponent(String)}
     */
    public SyncedEnchantmentComponent(String id) {
        super(id);
    }
}
