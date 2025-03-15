package moffy.ticex.block.entity;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.client.SafeClient;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

public class RFFurnaceBlockEntity extends SmelteryComponentBlockEntity implements ITankBlockEntity{

    private boolean isCreative;
    
    private int maxEnergyRate;
    private int lastStrength;

    private RFFurnaceFluidTank tank;
    private RFFurnaceEnergyStorage energyStorage;
    private LazyOptional<IFluidHandler> fluidHolder;
    private LazyOptional<IEnergyStorage> energyHolder;

    private int tankCapacity;

    public RFFurnaceBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, boolean isCreative) {
        super(pType, pPos, pBlockState);
        this.isCreative = isCreative;
        this.maxEnergyRate = TicEX.RF_FURNACE_RATE_CAPACITY.get();
        this.energyStorage = new RFFurnaceEnergyStorage(maxEnergyRate);
        this.lastStrength = -1;

        this.tankCapacity = FluidType.BUCKET_VOLUME;
        this.tank = new RFFurnaceFluidTank(tankCapacity, this);
        this.fluidHolder = LazyOptional.of(()->this.tank);
        this.energyHolder = LazyOptional.of(()->this.energyStorage);
    }

    public RFFurnaceEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getMaxEnergyRate() {
        return maxEnergyRate;
    }

    public boolean isCreative() {
        return isCreative;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER){
            return fluidHolder.cast();
        } else if(cap == ForgeCapabilities.ENERGY){
            return energyHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHolder.invalidate();
        energyHolder.invalidate();
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RFFurnaceBlockEntity pBlockEntity){
        int energyRate = pBlockEntity.getEnergyStorage().getEnergyRate();
        int extracted = pBlockEntity.getEnergyStorage().extractEnergy(1000, false);

        int fuelIndex = pBlockEntity.isCreative() ? 20 : Math.round(20 * (energyRate / (float)pBlockEntity.getMaxEnergyRate()));

        if(pBlockEntity.isCreative() || extracted >= 1){
            if(fuelIndex == 0){
                pBlockEntity.updateFluidTo(FluidStack.EMPTY);
            } else {
                pBlockEntity.updateFluidTo(new FluidStack(TicEXRegistry.RF_FURNACE_FUELS.get(fuelIndex - 1).get(), FluidType.BUCKET_VOLUME));
            }
        } else {
            pBlockEntity.getEnergyStorage().setEnergyRate(0);
            pBlockEntity.updateFluidTo(FluidStack.EMPTY);
        }
    }

    @Override
    public void onTankContentsChanged() {
        ITankBlockEntity.super.onTankContentsChanged();
        if (this.level != null) {
        level.getLightEngine().checkBlock(this.worldPosition);
        this.requestModelDataUpdate();
        }
    }

    @Override
    public int getLastStrength() {
        return lastStrength;
    }

    @Override
    public FluidTankAnimated getTank() {
        return this.tank;
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                        .with(ModelProperties.FLUID_STACK, tank.getFluid())
                        .with(ModelProperties.TANK_CAPACITY, tank.getCapacity()).build();
    }

    public void updateTank(CompoundTag nbt) {
        if (nbt.isEmpty()) {
            this.tank.setFluid(FluidStack.EMPTY);
        } else {
            this.tank.readFromNBT(nbt);
            if (level != null) {
                this.level.getLightEngine().checkBlock(worldPosition);
            }
        }
      }

    @Override
    public void updateFluidTo(FluidStack fluid) {
        this.tank.setFluid(fluid);
            if (this.isFluidInModel()) {
                SafeClient.updateFluidModel(this.getTE(), tank, this.tankCapacity, this.tankCapacity);
            }
        }

    @Override
    public void setLastStrength(int arg0) {
        this.lastStrength = arg0;
    }
    
    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    public void load(CompoundTag tag) {
        tank.setCapacity(this.tankCapacity);
        updateTank(tag.getCompound(NBTTags.TANK));
        super.load(tag);
    }

    @Override
    public void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        if (!tank.isEmpty()) {
        tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
        }
    }
}
