package com.birblett.registry;

import com.birblett.Supplementary;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Attributes used for common traits in the mod
 */
public class SupplementaryAttributes {

    /**
     * Draw speed for bows, crossbows, and tridents. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute DRAW_SPEED = new ClampedEntityAttribute("attribute.name.generic." +
            Supplementary.MODID + ".draw_speed", 10.0, 0.0, 1024.0).setTracked(true);
    /**
     * Overall mining speed. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute MINING_SPEED = new ClampedEntityAttribute("attribute.name.generic." +
            Supplementary.MODID + ".mining_speed", 10.0, 0.0, 1024.0).setTracked(true);
    /**
     * Mining speed if tool is effective. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute EFFECTIVE_MINING_SPEED = new ClampedEntityAttribute("attribute.name.generic." +
            Supplementary.MODID + ".effective_mining_speed", 10.0, 0.0, 1024.0).setTracked(true);
    /**
     * Mining speed if tool is not effective. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute INEFFECTIVE_MINING_SPEED = new ClampedEntityAttribute("attribute.name.generic." +
            Supplementary.MODID + ".ineffective_mining_speed", 10.0, 0.0, 1024.0).setTracked(true);


    public static void register() {
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "draw_speed"), DRAW_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "mining_speed"), MINING_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "effective_mining_speed"), EFFECTIVE_MINING_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "ineffective_mining_speed"), INEFFECTIVE_MINING_SPEED);
    }

}
