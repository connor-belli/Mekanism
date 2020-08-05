package mekanism.api.chemical;

import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.attribute.ChemicalAttribute;
import mekanism.api.providers.IChemicalProvider;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.IHasTranslationKey;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class Chemical<CHEMICAL extends Chemical<CHEMICAL>> extends ForgeRegistryEntry<CHEMICAL> implements IChemicalProvider<CHEMICAL>, IHasTextComponent,
      IHasTranslationKey {

    private final Map<Class<? extends ChemicalAttribute>, ChemicalAttribute> attributeMap;

    private final ResourceLocation iconLocation;
    private final boolean hidden;
    private final int tint;

    private String translationKey;

    protected Chemical(ChemicalBuilder<CHEMICAL, ?> builder) {
        this.attributeMap = builder.getAttributeMap();
        this.iconLocation = builder.getTexture();
        this.tint = builder.getColor();
        this.hidden = builder.isHidden();
    }

    @Override
    public CHEMICAL getChemical() {
        return (CHEMICAL) this;
    }

    @Override
    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = getDefaultTranslationKey();
        }
        return translationKey;
    }

    /**
     * Whether this chemical has an attribute of a certain type.
     *
     * @param type attribute type to check
     *
     * @return if this chemical has the attribute
     */
    public boolean has(Class<? extends ChemicalAttribute> type) {
        return attributeMap.containsKey(type);
    }

    /**
     * Gets the attribute instance of a certain type, or null if it doesn't exist.
     *
     * @param type attribute type to get
     *
     * @return attribute instance
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends ChemicalAttribute> T get(Class<T> type) {
        return (T) attributeMap.get(type);
    }

    /**
     * Adds an attribute to this chemical's attribute map. Will overwrite any existing attribute with the same type.
     *
     * @param attribute attribute to add to this chemical
     */
    public void addAttribute(ChemicalAttribute attribute) {
        attributeMap.put(attribute.getClass(), attribute);
    }

    /**
     * Gets all attribute instances associated with this chemical type.
     *
     * @return collection of attribute instances
     */
    public Collection<ChemicalAttribute> getAttributes() {
        return attributeMap.values();
    }

    /**
     * Gets all attribute types associated with this chemical type.
     *
     * @return collection of attribute types
     */
    public Collection<Class<? extends ChemicalAttribute>> getAttributeTypes() {
        return attributeMap.keySet();
    }

    /**
     * Writes this Chemical to a defined tag compound.
     *
     * @param nbtTags - tag compound to write this Chemical to
     *
     * @return the tag compound this Chemical was written to
     */
    public abstract CompoundNBT write(CompoundNBT nbtTags);

    protected abstract String getDefaultTranslationKey();

    @Override
    public ITextComponent getTextComponent() {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * Gets the resource location of the icon associated with this Chemical.
     *
     * @return The resource location of the icon
     */
    public ResourceLocation getIcon() {
        return iconLocation;
    }

    /**
     * Get the tint for rendering the chemical
     *
     * @return int representation of color in 0xRRGGBB format
     */
    public int getTint() {
        return tint;
    }

    /**
     * Whether or not this chemical is hidden.
     *
     * @return if this chemical is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    public abstract boolean isIn(Tag<CHEMICAL> tag);

    public abstract Set<ResourceLocation> getTags();

    public abstract boolean isEmptyType();
}