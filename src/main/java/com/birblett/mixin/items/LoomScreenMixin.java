package com.birblett.mixin.items;

import com.birblett.trinkets.CapeItem;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

/**
 * Allows for applying patterns to capes
 */
@Mixin(LoomScreen.class)
public class LoomScreenMixin {

    @Unique private ItemStack displayedStack;

    @ModifyVariable(method = "onInventoryChanged", at = @At(target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", value = "INVOKE"), ordinal = 0)
    private ItemStack bannerCastWorkaround(ItemStack itemStack) {
        // Store cape ref elsewhere and replace with a dummy item so the BannerItem cast does not crash the game
        displayedStack = itemStack;
        if (!(itemStack.getItem() instanceof BannerItem)) {
            return new ItemStack(Items.WHITE_BANNER);
        }
        return itemStack;
    }

    @ModifyArg(method = "onInventoryChanged", at = @At(target = "Lnet/minecraft/block/entity/BannerBlockEntity;getPatterns" +
               "FromNbt(Lnet/minecraft/util/DyeColor;Lnet/minecraft/nbt/NbtList;)Ljava/util/List;", value = "INVOKE"))
    private DyeColor allowCapesInPlaceOfBanners(DyeColor baseColor) {
        // Get base color of provided cape here if applicable
        if (!(displayedStack.getItem() instanceof BannerItem)) {
            if (displayedStack.getItem() instanceof CapeItem) {
                return CapeItem.getBaseColor(displayedStack);
            }
            return baseColor;
        }
        return ((BannerItem) displayedStack.getItem()).getColor();
    }

    @ModifyArg(method = "onInventoryChanged", at = @At(target = "Lnet/minecraft/block/entity/BannerBlockEntity;getPatternListNbt(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NbtList;", value = "INVOKE"))
    private ItemStack fixStack(ItemStack itemStack) {
        // Replace getPatternList arg with the previously saved stack
        return displayedStack;
    }
}
