package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface SimpleEntityComponent<T> extends Component {

    T getValue();
    void setValue(T object);
}
