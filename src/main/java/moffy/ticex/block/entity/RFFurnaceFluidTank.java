package moffy.ticex.block.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class RFFurnaceFluidTank extends FluidTankAnimated{

    private RFFurnaceBlockEntity parent;

    public RFFurnaceFluidTank(int capacity, RFFurnaceBlockEntity parent) {
        super(capacity, parent);
        this.parent = parent;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        parent.saveSynced(parent.serializeNBT());

        if (this.parent instanceof IFluidTankUpdater) {
            ((IFluidTankUpdater)this.parent).onTankContentsChanged();
        }

        this.parent.setChanged();
        Level level = this.parent.getLevel();
        if (level != null && !level.isClientSide) {
            TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(this.parent.getBlockPos(), this.getFluid()), level, this.parent.getBlockPos());
        }
    }
    
}
