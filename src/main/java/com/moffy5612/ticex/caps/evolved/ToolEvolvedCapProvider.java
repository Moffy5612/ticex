package com.moffy5612.ticex.caps.evolved;

import java.util.function.Supplier;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolEvolvedCapProvider implements IToolCapabilityProvider{
    
    public EvolvedModuleHost host;
    public EvolvedOPStorage opStorage;

    public ToolEvolvedCapProvider (ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        host = new EvolvedModuleHost(toolSupplier.get());
        opStorage = new EvolvedOPStorage(host, toolSupplier.get());
    }

    @SuppressWarnings("null")
    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(
            (capability == DECapabilities.MODULE_HOST_CAPABILITY || capability == DECapabilities.PROPERTY_PROVIDER_CAPABILITY) 
            && tool.getModifierLevel(TicEXModuleProvider.MODIFIER_EVOLVED.get()) > 0) {
            return LazyOptional.of(()->host).cast();
        } else if (capability == DECapabilities.OP_STORAGE && tool.getModifierLevel(TicEXModuleProvider.MODIFIER_EVOLVED.get()) > 0){
            return LazyOptional.of(()->opStorage).cast();
        }
        return LazyOptional.empty();
    }
}
