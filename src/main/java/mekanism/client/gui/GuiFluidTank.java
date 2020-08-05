package mekanism.client.gui;


import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.tab.GuiContainerEditModeTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiFluidTank extends GuiMekanismTile<TileEntityFluidTank, MekanismTileContainer<TileEntityFluidTank>> {

    public GuiFluidTank(MekanismTileContainer<TileEntityFluidTank> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiContainerEditModeTab<>(this, tile));
        addButton(new GuiSecurityTab<>(this, tile));
        addButton(new GuiFluidGauge(() -> tile.fluidTank, () -> tile.getFluidTanks(null), GaugeType.WIDE, this, 48, 18));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        renderTitleText();
        drawString(MekanismLang.INVENTORY.translate(), 8, getYSize() - 96 + 2, titleTextColor());
        super.drawForegroundText(mouseX, mouseY);
    }
}