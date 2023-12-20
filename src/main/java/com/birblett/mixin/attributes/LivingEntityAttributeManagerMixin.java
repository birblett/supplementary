package com.birblett.mixin.attributes;

import com.birblett.lib.creational.EnchantmentBuilder;
import com.birblett.registry.SupplementaryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Manages attribute modifiers bound to enchantments.
 */
@Mixin(LivingEntity.class)
public class LivingEntityAttributeManagerMixin {

    @Unique private final List<Pair<EntityAttribute, UUID>> attributesList = new ArrayList<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void refreshAttributes(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld() instanceof ServerWorld) {
            for (Pair<EntityAttribute, UUID> attr : this.attributesList) {
                EntityAttributeInstance instance = self.getAttributeInstance(attr.getLeft());
                if (instance != null) {
                    instance.removeModifier(attr.getRight());
                }
            }
            this.attributesList.clear();
            for (EquipmentSlot slot : SupplementaryEnchantments.ALL_SLOTS) {
                ItemStack stack = self.getEquippedStack(slot);
                EnchantmentHelper.fromNbt(stack.getEnchantments()).forEach((ench, lvl) -> {
                    if (ench instanceof EnchantmentBuilder e && !e.getAttributes().isEmpty()) {
                        e.getAttributes().forEach((attr) -> {
                            EntityAttributeInstance instance = self.getAttributeInstance(attr.attribute());
                            if (Arrays.stream(e.getSlotTypes()).anyMatch(s -> s == slot) && instance != null && self.getAttributeInstance(
                                    attr.attribute()) != null) {
                                EntityAttributeModifier mod = new EntityAttributeModifier(attr.baseName(), attr.scaling().apply(self, stack, lvl),
                                        attr.operation());
                                instance.addTemporaryModifier(mod);
                                UUID id = mod.getId();
                                this.attributesList.add(new Pair<>(attr.attribute(), id));
                            }
                        });
                    }
                });
            }
        }
    }
}