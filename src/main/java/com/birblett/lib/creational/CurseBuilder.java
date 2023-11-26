package com.birblett.lib.creational;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CurseBuilder extends EnchantmentBuilder {

    private final Function<Integer, Integer> cursePointProvider;

    /**
     * <hr><center><h1>Constructor</h1></center><hr>
     *
     * See {@link com.birblett.lib.creational.EnchantmentBuilder#EnchantmentBuilder(String, Rarity, EnchantmentTarget, EquipmentSlot[])}
     * for information on non-curse specific parameters.
     * @param cursePoints Function mapping level to number of curse points.
     */
    public CurseBuilder(String identifier, Rarity weight, @Nullable EnchantmentTarget type, EquipmentSlot[] slotTypes, Function<Integer, Integer> cursePoints) {
        super(identifier, weight, type, slotTypes);
        this.cursePointProvider = cursePoints;
    }

    public int getCursePoints(int level) {
        return cursePointProvider.apply(level);
    }

}
