package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;

/**
 * Lightweight component interface used for easy storage of data and server-client sync. Data storage handled
 * automatically.
 * @see Component
 * @see SimpleEntityComponent
 */
public interface SimpleComponent<T> extends Component {

    T getValue();
    T getDefaultValue();
    void setValue(T object);
}
