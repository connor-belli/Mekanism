package mekanism.client.gui.machine;


import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiRedstoneControlTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiAdvancedElectricMachine<TILE extends TileEntityAdvancedElectricMachine, CONTAINER extends MekanismTileContainer<TILE>> extends
      GuiConfigurableTile<TILE, CONTAINER> {

    public GuiAdvancedElectricMachine(CONTAINER container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiRedstoneControlTab(this, tile));
        addButton(new GuiUpgradeTab(this, tile));
        addButton(new GuiSecurityTab<>(this, tile));
        addButton(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
        addButton(new GuiEnergyTab(tile.getEnergyContainer(), this));
        addButton(new GuiProgress(tile::getScaledProgress, ProgressType.BAR, this, 86, 38).jeiCategory(tile));
        addButton(new GuiChemicalBar<>(this, GuiChemicalBar.getProvider(tile.gasTank, tile.getGasTanks(null)), 68, 36, 6, 12, false));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        renderTitleText();
        drawString(MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        super.drawForegroundText(mouseX, mouseY);
    }
}