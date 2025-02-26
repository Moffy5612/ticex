package com.moffy5612.ticex.caps.evolved;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.moffy5612.ticex.modifiers.EvolvedModifierTool;
import com.moffy5612.ticex.utils.TicEXUtils;

import net.minecraft.nbt.CompoundTag;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EvolvedOPStorage extends ModularOPStorage{
    private IToolStackView tool;

    public EvolvedOPStorage(EvolvedModuleHost host, IToolStackView tool) {
        super(host, EquipCfg.getBaseStaffEnergy(host.getHostTechLevel()), EquipCfg.getBaseStaffTransfer(host.getHostTechLevel()));
        this.tool = tool;
        this.setIOMode(true);
        this.readFromPersistentData();
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        writeToPersistentData();
        return extracted;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        writeToPersistentData();
        return received;
    }

    @Override
    public OPStorage setCapacity(long capacity) {
        OPStorage opStorage = super.setCapacity(capacity);
        writeToPersistentData();
        return opStorage;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        long received = super.receiveOP(maxReceive, simulate);
        writeToPersistentData();
        return received;
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        long extracted = super.extractOP(maxExtract, simulate);
        writeToPersistentData();
        return extracted;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("energy", TicEXUtils.longToIntWithPercentage(getOPStored()));
        nbt.putInt("capacity", TicEXUtils.longToIntWithPercentage(getMaxOPStored()));
        return nbt;
    }

    private void writeToPersistentData(){
        tool.getPersistentData().put(EvolvedModifierTool.OP_STORAGE_LOCATION, serializeNBT());
    }

    private void readFromPersistentData(){
        deserializeNBT(tool.getPersistentData().getCompound(EvolvedModifierTool.OP_STORAGE_LOCATION));
    }
}
