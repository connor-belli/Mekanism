package mekanism.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import mekanism.api.Coord4D;
import mekanism.api.IHeatTransfer;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlock;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.HeatUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityResistiveHeater extends TileEntityMekanism implements IHeatTransfer, IComputerIntegration {

    private static final int[] SLOTS = {0};

    private static final String[] methods = new String[]{"getEnergy", "getMaxEnergy", "getTemperature", "setEnergyUsage"};
    public double energyUsage = 100;
    public double temperature;
    public double heatToAbsorb = 0;
    //TODO: Figure out sound
    public float soundScale = 1;
    public double lastEnvironmentLoss;

    public TileEntityResistiveHeater() {
        super(MekanismBlock.RESISTIVE_HEATER);
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            boolean packet = false;
            ChargeUtils.discharge(0, this);
            double toUse = 0;
            if (MekanismUtils.canFunction(this)) {
                toUse = Math.min(getEnergy(), energyUsage);
                heatToAbsorb += toUse / MekanismConfig.current().general.energyPerHeat.val();
                setEnergy(getEnergy() - toUse);
            }

            setActive(toUse > 0);
            double[] loss = simulateHeat();
            applyTemperatureChange();
            lastEnvironmentLoss = loss[1];
            float newSoundScale = (float) Math.max(0, toUse / 1E5);
            if (Math.abs(newSoundScale - soundScale) > 0.01) {
                packet = true;
            }

            soundScale = newSoundScale;
            if (packet) {
                Mekanism.packetHandler.sendUpdatePacket(this);
            }
        }
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        return side == getLeftSide() || side == getRightSide();
    }

    @Override
    public boolean canSetFacing(@Nonnull Direction facing) {
        return facing != Direction.DOWN && facing != Direction.UP;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        energyUsage = nbtTags.getDouble("energyUsage");
        temperature = nbtTags.getDouble("temperature");
        setMaxEnergy(energyUsage * 400);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.putDouble("energyUsage", energyUsage);
        nbtTags.putDouble("temperature", temperature);
        return nbtTags;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            energyUsage = MekanismUtils.convertToJoules(dataStream.readInt());
            setMaxEnergy(energyUsage * 400);
            return;
        }

        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            energyUsage = dataStream.readDouble();
            temperature = dataStream.readDouble();
            setMaxEnergy(dataStream.readDouble());
            soundScale = dataStream.readFloat();
            lastEnvironmentLoss = dataStream.readDouble();
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);

        data.add(energyUsage);
        data.add(temperature);
        data.add(getMaxEnergy());
        data.add(soundScale);

        data.add(lastEnvironmentLoss);
        return data;
    }

    @Override
    public double getTemp() {
        return temperature;
    }

    @Override
    public double getInverseConductionCoefficient() {
        return 5;
    }

    @Override
    public double getInsulationCoefficient(Direction side) {
        return 1000;
    }

    @Override
    public void transferHeatTo(double heat) {
        heatToAbsorb += heat;
    }

    @Override
    public double[] simulateHeat() {
        return HeatUtils.simulate(this);
    }

    @Override
    public double applyTemperatureChange() {
        temperature += heatToAbsorb;
        heatToAbsorb = 0;
        return temperature;
    }

    @Override
    public boolean canConnectHeat(Direction side) {
        return true;
    }

    @Override
    public IHeatTransfer getAdjacent(Direction side) {
        TileEntity adj = Coord4D.get(this).offset(side).getTileEntity(world);
        if (CapabilityUtils.hasCapability(adj, Capabilities.HEAT_TRANSFER_CAPABILITY, side.getOpposite())) {
            return CapabilityUtils.getCapability(adj, Capabilities.HEAT_TRANSFER_CAPABILITY, side.getOpposite());
        }
        return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, Direction side) {
        return capability == Capabilities.HEAT_TRANSFER_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, Direction side) {
        if (capability == Capabilities.HEAT_TRANSFER_CAPABILITY) {
            return Capabilities.HEAT_TRANSFER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean lightUpdate() {
        return true;
    }

    @Override
    public String[] getMethods() {
        return methods;
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        switch (method) {
            case 0:
                return new Object[]{getEnergy()};
            case 1:
                return new Object[]{getMaxEnergy()};
            case 2:
                return new Object[]{temperature};
            case 3:
                if (arguments.length == 1) {
                    if (arguments[0] instanceof Double) {
                        energyUsage = (Double) arguments[0];
                        return new Object[]{"Set energy usage."};
                    }
                }
                return new Object[]{"Invalid parameters."};
            default:
                throw new NoSuchMethodException();
        }
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return ChargeUtils.canBeDischarged(stack);
    }
}