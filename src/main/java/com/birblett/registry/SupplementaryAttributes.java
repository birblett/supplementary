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
    public static final EntityAttribute DRAW_SPEED = supplementaryAttribute("draw_speed", 10.0, 0.0, 1024.0);
    /**
     * Overall mining speed. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute MINING_SPEED = supplementaryAttribute("mining_speed", 10.0, 0.0,
            1024.0);
    /**
     * Mining speed if tool is effective. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute EFFECTIVE_MINING_SPEED = supplementaryAttribute("effective_mining_speed", 10.0,
            0.0, 1024.0);
    /**
     * Mining speed if tool is not effective. Base is 10.0, final multiplier is {@code total_value / 10.}
     */
    public static final EntityAttribute INEFFECTIVE_MINING_SPEED = supplementaryAttribute("ineffective_mining_speed",
            10.0, 0.0, 1024.0);
    public static final EntityAttribute NO_FOV_MOVE_SPEED = supplementaryAttribute("no_fov_move_speed", 10.0,
            0.0, 1024.0);


    public static void register() {
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "draw_speed"), DRAW_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "mining_speed"), MINING_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "effective_mining_speed"), EFFECTIVE_MINING_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "ineffective_mining_speed"), INEFFECTIVE_MINING_SPEED);
        Registry.register(Registries.ATTRIBUTE, new Identifier(Supplementary.MODID, "no_fov_move_speed"), NO_FOV_MOVE_SPEED);
    }

    private static EntityAttribute supplementaryAttribute(String id, double fallback, double min, double max) {
        return new ClampedEntityAttribute("attribute.name.generic." + Supplementary.MODID + "." + id,
                fallback, min, max).setTracked(true);
    }

}
