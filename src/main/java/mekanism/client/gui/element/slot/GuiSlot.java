package mekanism.client.gui.element.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.jei.interfaces.IJEIGhostTarget;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class GuiSlot extends GuiTexturedElement implements IJEIGhostTarget {

    private static final int INVALID_SLOT_COLOR = MekanismRenderer.getColorARGB(EnumColor.DARK_RED, 0.8F);
    public static final int DEFAULT_HOVER_COLOR = 0x80FFFFFF;
    private boolean hasValidityCheck;
    private Supplier<ItemStack> validityCheck = () -> ItemStack.EMPTY;
    private Supplier<SlotOverlay> overlaySupplier;
    private IntSupplier overlayColorSupplier;
    private SlotOverlay overlay;
    private IHoverable onHover;
    private IClickable onClick;
    private boolean renderHover;
    private boolean renderAboveSlots;

    @Nullable
    private IGhostIngredientConsumer ghostHandler;

    public GuiSlot(SlotType type, IGuiWrapper gui, int x, int y) {
        super(type.getTexture(), gui, x, y, type.getWidth(), type.getHeight());
        active = false;
    }

    public GuiSlot validity(Supplier<ItemStack> validityCheck) {
        hasValidityCheck = true;
        this.validityCheck = validityCheck;
        return this;
    }

    public GuiSlot hover(IHoverable onHover) {
        this.onHover = onHover;
        return this;
    }

    public GuiSlot click(IClickable onClick) {
        this.onClick = onClick;
        return this;
    }

    public GuiSlot with(SlotOverlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public GuiSlot overlayColor(IntSupplier colorSupplier) {
        overlayColorSupplier = colorSupplier;
        return this;
    }

    public GuiSlot with(Supplier<SlotOverlay> overlaySupplier) {
        this.overlaySupplier = overlaySupplier;
        return this;
    }

    public GuiSlot setRenderHover(boolean renderHover) {
        this.renderHover = renderHover;
        return this;
    }

    public GuiSlot setGhostHandler(@Nullable IGhostIngredientConsumer ghostHandler) {
        this.ghostHandler = ghostHandler;
        return this;
    }

    public GuiSlot setRenderAboveSlots() {
        this.renderAboveSlots = true;
        return this;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (!renderAboveSlots) {
            draw(mouseX, mouseY);
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        if (renderAboveSlots) {
            draw(mouseX, mouseY);
        }
    }

    private void draw(int mouseX, int mouseY) {
        minecraft.textureManager.bindTexture(getResource());
        blit(x, y, 0, 0, width, height, width, height);
        if (hasValidityCheck) {
            ItemStack invalid = validityCheck.get();
            if (!invalid.isEmpty()) {
                int xPos = x + 1;
                int yPos = y + 1;
                fill(xPos, yPos, xPos + 16, yPos + 16, INVALID_SLOT_COLOR);
                MekanismRenderer.resetColor();
                guiObj.renderItem(invalid, xPos, yPos);
            }
        }
        if (overlaySupplier != null) {
            overlay = overlaySupplier.get();
        }
        if (overlay != null) {
            minecraft.textureManager.bindTexture(overlay.getTexture());
            blit(x, y, 0, 0, overlay.getWidth(), overlay.getHeight(), overlay.getWidth(), overlay.getHeight());
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        if (renderHover && isHovered()) {
            int xPos = relativeX + 1;
            int yPos = relativeY + 1;
            fill(xPos, yPos, xPos + 16, yPos + 16, DEFAULT_HOVER_COLOR);
            MekanismRenderer.resetColor();
        }
        if (overlayColorSupplier != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translated(0, 0, 10);
            int xPos = relativeX + 1;
            int yPos = relativeY + 1;
            fill(xPos, yPos, xPos + 16, yPos + 16, overlayColorSupplier.getAsInt());
            RenderSystem.translated(0, 0, -10);
            RenderSystem.popMatrix();
            MekanismRenderer.resetColor();
        }
        if (isHovered()) {
            //TODO: Should it pass it the proper mouseX and mouseY. Probably, though buttons may have to be redone slightly then
            renderToolTip(mouseX - guiObj.getLeft(), mouseY - guiObj.getTop());
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (onHover != null) {
            onHover.onHover(this, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onClick != null && isValidClickButton(button)) {
            if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
                onClick.onClick(this, (int) mouseX, (int) mouseY);
                playDownSound(Minecraft.getInstance().getSoundHandler());
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public IGhostIngredientConsumer getGhostHandler() {
        return ghostHandler;
    }

    @Override
    public int borderSize() {
        return 1;
    }
}