package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.LivingEntity;

public interface EntityComponent extends Component {

    LivingEntity getEntity();
    void setEntity(LivingEntity livingEntity);
}
