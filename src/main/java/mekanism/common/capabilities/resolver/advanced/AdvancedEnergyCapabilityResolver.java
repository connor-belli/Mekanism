package mekanism.common.capabilities.resolver.advanced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.energy.ISidedStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.proxy.ProxyStrictEnergyHandler;
import mekanism.common.capabilities.resolver.ICapabilityResolver;
import mekanism.common.integration.energy.EnergyCompatUtils;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedEnergyCapabilityResolver implements ICapabilityResolver {

    private final Map<Capability<?>, LazyOptional<?>> cachedCapabilities = new HashMap<>();
    private final Map<Capability<?>, LazyOptional<?>> cachedReadOnlyCapabilities = new HashMap<>();
    private final IStrictEnergyHandler handler;
    private final IStrictEnergyHandler readOnlyHandler;

    public AdvancedEnergyCapabilityResolver(ISidedStrictEnergyHandler handler) {
        this.handler = handler;
        this.readOnlyHandler = new ProxyStrictEnergyHandler(handler, null, null);
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return EnergyCompatUtils.getEnabledEnergyCapabilities();
    }

    @Override
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable Direction side) {
        //TODO: Reduce duplicate code
        if (side == null) {
            if (cachedReadOnlyCapabilities.containsKey(capability)) {
                //If we already contain a cached object for this lazy optional then get it and use it
                LazyOptional<?> cachedCapability = cachedReadOnlyCapabilities.get(capability);
                if (cachedCapability.isPresent()) {
                    //If the capability is still present (valid), just return the cached object
                    return cachedCapability.cast();
                }
            }
            LazyOptional<T> uncachedCapability = EnergyCompatUtils.getEnergyCapability(capability, readOnlyHandler);
            cachedReadOnlyCapabilities.put(capability, uncachedCapability);
            return uncachedCapability;
        }
        if (cachedCapabilities.containsKey(capability)) {
            //If we already contain a cached object for this lazy optional then get it and use it
            LazyOptional<?> cachedCapability = cachedCapabilities.get(capability);
            if (cachedCapability.isPresent()) {
                //If the capability is still present (valid), just return the cached object
                return cachedCapability.cast();
            }
        }
        LazyOptional<T> uncachedCapability = EnergyCompatUtils.getEnergyCapability(capability, handler);
        cachedCapabilities.put(capability, uncachedCapability);
        return uncachedCapability;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        if (side == null) {
            invalidate(cachedReadOnlyCapabilities.get(capability));
        } else {
            invalidate(cachedCapabilities.get(capability));
        }
    }

    @Override
    public void invalidateAll() {
        cachedCapabilities.values().forEach(this::invalidate);
        cachedReadOnlyCapabilities.values().forEach(this::invalidate);
    }

    private void invalidate(@Nullable LazyOptional<?> cachedCapability) {
        if (cachedCapability != null) {
            cachedCapability.invalidate();
        }
    }
}