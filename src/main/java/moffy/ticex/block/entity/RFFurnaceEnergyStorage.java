package moffy.ticex.block.entity;

import net.minecraftforge.energy.EnergyStorage;

public class RFFurnaceEnergyStorage extends EnergyStorage{

    private int energyRate;
    private int maxEnergyRate;

    public RFFurnaceEnergyStorage(int maxEnergyRate) {
            super(maxEnergyRate);
            this.maxEnergyRate = maxEnergyRate;
        }
    
        @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.min(maxEnergyRate, maxReceive);
        if(!simulate){
            this.energyRate = received;
            this.energy = received;
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.min(this.energy, maxExtract);
        if(!simulate){
            this.energy -= extracted;
        }
        return extracted; 
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyRate;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public int getEnergyRate() {
        return energyRate;
    }

    public int getMaxEnergyRate() {
        return maxEnergyRate;
    }

    public void setEnergyRate(int energyRate) {
        this.energyRate = energyRate;
    }
}
