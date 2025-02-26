package com.moffy5612.ticex.caps.katarize;

import java.util.function.Supplier;

import com.moffy5612.ticex.handlers.TicEXModuleProvider;

import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolKatarizeCapProvider implements IToolCapabilityProvider{
	
	public KatarizeCap katarize;

    public ToolKatarizeCapProvider (ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        katarize = new KatarizeCap();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
    	if(
			(
				capability == PECapabilities.CHARGE_ITEM_CAPABILITY 
				|| capability == PECapabilities.MODE_CHANGER_ITEM_CAPABILITY 
				|| capability == PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY
			) 
    		&& tool.getModifierLevel(TicEXModuleProvider.MODIFIER_KATARIZE.get()) > 0
    	) {
    		return LazyOptional.of(()->katarize).cast();
    	}
        return LazyOptional.empty();
    }
}