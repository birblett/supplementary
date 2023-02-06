package com.birblett.lib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface EntityComponent extends Component {

    Entity getEntity();
    void setEntity(Entity livingEntity);
}
