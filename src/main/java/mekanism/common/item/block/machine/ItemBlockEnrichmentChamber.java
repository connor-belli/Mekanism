package mekanism.common.item.block.machine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.common.Upgrade;
import mekanism.common.block.machine.BlockEnrichmentChamber;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.integration.forgeenergy.ForgeEnergyItemWrapper;
import mekanism.common.item.IItemEnergized;
import mekanism.common.item.IItemSustainedInventory;
import mekanism.common.item.block.ItemBlockAdvancedTooltip;
import mekanism.common.security.ISecurityItem;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlockEnrichmentChamber extends ItemBlockAdvancedTooltip implements IItemEnergized, IItemSustainedInventory, ISecurityItem {

    public ItemBlockEnrichmentChamber(BlockEnrichmentChamber block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addDetails(@Nonnull ItemStack itemstack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
        list.add(SecurityUtils.getOwnerDisplay(Minecraft.getInstance().player, MekanismClient.clientUUIDMap.get(getOwnerUUID(itemstack))));
        list.add(EnumColor.GREY + LangUtils.localize("gui.security") + ": " + SecurityUtils.getSecurityDisplay(itemstack, Dist.CLIENT));
        if (SecurityUtils.isOverridden(itemstack, Dist.CLIENT)) {
            list.add(EnumColor.RED + "(" + LangUtils.localize("gui.overridden") + ")");
        }
        list.add(EnumColor.BRIGHT_GREEN + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY
                 + MekanismUtils.getEnergyDisplay(getEnergy(itemstack), getMaxEnergy(itemstack)));
        list.add(EnumColor.AQUA + LangUtils.localize("tooltip.inventory") + ": " + EnumColor.GREY +
                 LangUtils.transYesNo(getInventory(itemstack) != null && !getInventory(itemstack).isEmpty()));
        if (ItemDataUtils.hasData(itemstack, "upgrades")) {
            Map<Upgrade, Integer> upgrades = Upgrade.buildMap(ItemDataUtils.getDataMap(itemstack));
            for (Entry<Upgrade, Integer> entry : upgrades.entrySet()) {
                list.add(entry.getKey().getColor() + "- " + entry.getKey().getName() + (entry.getKey().canMultiply() ? ": " + EnumColor.GREY + "x" + entry.getValue() : ""));
            }
        }
    }

    @Override
    public double getMaxEnergy(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ItemBlockEnrichmentChamber) {
            return MekanismUtils.getMaxEnergy(itemStack, ((BlockEnrichmentChamber) (((ItemBlockEnrichmentChamber) item).block)).getStorage());
        }
        return 0;
    }

    @Override
    public double getMaxTransfer(ItemStack itemStack) {
        return getMaxEnergy(itemStack) * 0.005;
    }

    @Override
    public boolean canReceive(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean canSend(ItemStack itemStack) {
        return false;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemCapabilityWrapper(stack, new ForgeEnergyItemWrapper());
    }
}