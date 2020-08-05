package mekanism.client.gui.element.filter.miner;


import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.filter.GuiTagFilter;
import mekanism.common.base.TagCache;
import mekanism.common.content.miner.MinerTagFilter;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class GuiMinerTagFilter extends GuiTagFilter<MinerTagFilter, TileEntityDigitalMiner> implements GuiMinerFilterHelper {

    public static GuiMinerTagFilter create(IGuiWrapper gui, TileEntityDigitalMiner tile) {
        return new GuiMinerTagFilter(gui, (gui.getWidth() - 173) / 2, 30, tile, null);
    }

    public static GuiMinerTagFilter edit(IGuiWrapper gui, TileEntityDigitalMiner tile, MinerTagFilter filter) {
        return new GuiMinerTagFilter(gui, (gui.getWidth() - 173) / 2, 30, tile, filter);
    }

    private GuiMinerTagFilter(IGuiWrapper gui, int x, int y, TileEntityDigitalMiner tile, MinerTagFilter origFilter) {
        super(gui, x, y, 173, 90, tile, origFilter);
    }

    @Override
    protected void init() {
        super.init();
        addMinerDefaults(guiObj, filter, getSlotOffset(), this::addChild);
    }

    @Override
    protected MinerTagFilter createNewFilter() {
        return new MinerTagFilter();
    }

    @Nonnull
    @Override
    protected List<ItemStack> getRenderStacks() {
        if (filter.hasFilter()) {
            return TagCache.getBlockTagStacks(filter.getTagName());
        }
        return Collections.emptyList();
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        super.renderForeground(mouseX, mouseY);
        renderReplaceStack(guiObj, filter);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return tryClickReplaceStack(guiObj, mouseX, mouseY, button, getSlotOffset(), filter) || super.mouseClicked(mouseX, mouseY, button);
    }
}