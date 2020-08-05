package mekanism.common.item.gear;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseSpecialArmorMaterial implements IArmorMaterial {

    @Override
    public int getDurability(EquipmentSlotType slotType) {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.EMPTY;
    }
}