package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;

public interface SimpleEntityComponent extends Component {

    int getValue();
    void setValue(int i);
}
