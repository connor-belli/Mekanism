package mekanism.client.gui;


import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiLaserTractorBeam extends GuiMekanismTile<TileEntityLaserTractorBeam, MekanismTileContainer<TileEntityLaserTractorBeam>> {

    public GuiLaserTractorBeam(MekanismTileContainer<TileEntityLaserTractorBeam> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiSecurityTab<>(this, tile));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        renderTitleText();
        drawString(MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        super.drawForegroundText(mouseX, mouseY);
    }
}