package com.birblett.entities.damage_sources;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MagicSandDamage extends DamageSource {

    public MagicSandDamage(String name) {
        super(name);
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        return new TranslatableText("death.magic_sand", entity.getDisplayName());
    }
}
