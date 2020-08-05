package mekanism.api.chemical.pigment;

import mekanism.api.MekanismAPI;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public final class EmptyPigment extends Pigment {

    public EmptyPigment() {
        super(PigmentBuilder.builder().hidden());
        setRegistryName(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "empty_pigment"));
    }

    @Override
    public boolean isIn(@Nonnull Tag<Pigment> tags) {
        //Empty pigment is in no tags
        return false;
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> getTags() {
        return Collections.emptySet();
    }
}