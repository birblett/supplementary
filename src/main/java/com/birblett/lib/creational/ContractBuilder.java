package com.birblett.lib.creational;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ContractBuilder extends EnchantmentBuilder {

    public static Function<ItemStack, Boolean> NO_OP = stack -> true;
    private final Function<ItemStack, Boolean> isValidItem;
    public final int cursePointRequirement;

    /**
     * <hr><center><h1>Constructor</h1></center><hr>
     *
     * See {@link com.birblett.lib.creational.EnchantmentBuilder#EnchantmentBuilder(String, Rarity, EnchantmentTarget, EquipmentSlot[])}
     * for information on non-contract specific parameters.
     * @param isValidItem custom conditions on items the contract is being applied to.
     */
    public ContractBuilder(String identifier, Rarity weight, @Nullable EnchantmentTarget type, EquipmentSlot[] slotTypes, Function<ItemStack, Boolean> isValidItem, int cursePoints) {
        super(identifier, weight, type, slotTypes);
        this.isValidItem = isValidItem;
        this.cursePointRequirement = cursePoints;
    }

    /**
     * Overrides the existing isAcceptableItem implementation completely to also apply custom conditions.
     * @return whether the specified item is a valid item to apply this enchantment to
     */
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        AtomicInteger cursePoints = new AtomicInteger();
        EnchantmentHelper.fromNbt(stack.getEnchantments()).forEach((ench, lvl) -> {
            // contracts present reduce the total number of curse points
            if (ench instanceof ContractBuilder contract) {
                cursePoints.addAndGet(-contract.cursePointRequirement);
            }
            // if custom curse, add curse points based on enchantment level
            if (ench instanceof CurseBuilder curse) {
                cursePoints.addAndGet(curse.getCursePoints(lvl));
            }
            // default vanilla curses provide 2 curse points
            else if (ench.equals(Enchantments.BINDING_CURSE) || ench.equals(Enchantments.VANISHING_CURSE)) {
                cursePoints.addAndGet(2);
            }
            // modded curses provide 1 curse point
            else if (ench.isCursed()) {
                cursePoints.addAndGet(1);
            }
        });
        return cursePoints.get() >= this.cursePointRequirement && super.isAcceptableItem(stack) && this.isValidItem.apply(stack);
    }
}
