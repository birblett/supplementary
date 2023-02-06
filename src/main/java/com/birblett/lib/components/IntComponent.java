package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IntComponent extends Component {

    int getValue();
    void setValue(int level);
    EntityComponent getAttachedEntityComponent();
    void setAttachedEntityComponent(EntityComponent entityComponent);
    void increment();
}

