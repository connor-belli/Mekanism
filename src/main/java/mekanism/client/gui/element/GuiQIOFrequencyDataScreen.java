package mekanism.client.gui.element;

import java.util.function.Supplier;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiDigitalBar;
import mekanism.common.MekanismLang;
import mekanism.common.content.qio.QIOFrequency;
import net.minecraft.util.text.ITextComponent;

public class GuiQIOFrequencyDataScreen extends GuiInnerScreen {

    private Supplier<QIOFrequency> frequencySupplier;

    private GuiDigitalBar countBar, typeBar;

    public GuiQIOFrequencyDataScreen(IGuiWrapper gui, int x, int y, int width, int height, Supplier<QIOFrequency> frequencySupplier) {
        super(gui, x, y, width, height);
        this.frequencySupplier = frequencySupplier;
    }

    public void addBars(IGuiWrapper gui) {
        guiObj.addElement(countBar = new GuiDigitalBar(gui, new IBarInfoHandler() {
            @Override
            public double getLevel() {
                QIOFrequency freq = frequencySupplier.get();
                return freq != null ? (double) freq.getTotalItemCount() / (double) freq.getTotalItemCountCapacity() : 0;
            }
            @Override
            public ITextComponent getTooltip() {
                QIOFrequency freq = frequencySupplier.get();
                return freq != null ? MekanismLang.QIO_ITEMS_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      QIOFrequency.formatItemCount(freq.getTotalItemCount()), QIOFrequency.formatItemCount(freq.getTotalItemCountCapacity())) : null;
            }
        }, relativeX + (width / 4) - (50 / 2), relativeY + 20, 50));
        guiObj.addElement(typeBar = new GuiDigitalBar(gui, new IBarInfoHandler() {
            @Override
            public double getLevel() {
                QIOFrequency freq = frequencySupplier.get();
                return freq != null ? (double) freq.getTotalItemTypes(true) / (double) freq.getTotalItemTypeCapacity() : 0;
            }
            @Override
            public ITextComponent getTooltip() {
                QIOFrequency freq = frequencySupplier.get();
                return freq != null ? MekanismLang.QIO_ITEMS_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      QIOFrequency.formatItemCount(freq.getTotalItemTypes(true)), QIOFrequency.formatItemCount(freq.getTotalItemTypeCapacity())) : null;
            }
        }, relativeX + (3 * width / 4) - (50 / 2), relativeY + 20, 50));
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, int xAxis, int yAxis) {
        super.renderForeground(mouseX, mouseY, xAxis, yAxis);
        QIOFrequency freq = frequencySupplier.get();
        if (freq != null) {
            drawScaledTextScaledBound(MekanismLang.FREQUENCY.translate(freq.getName()), relativeX + 5, relativeY + 5, screenTextColor(), width - 10, 0.8F);
        }
        drawScaledCenteredText(MekanismLang.QIO_ITEMS.translate(), relativeX + (width / 4), relativeY + 32, screenTextColor(), 0.8F);
        drawScaledCenteredText(MekanismLang.QIO_TYPES.translate(), relativeX + (3 * width / 4), relativeY + 32, screenTextColor(), 0.8F);
    }
}
