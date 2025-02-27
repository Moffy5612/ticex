package com.moffy5612.ticex.caps.radiation_shield;

import java.util.function.Supplier;

import com.moffy5612.ticex.handlers.TicEXModuleProvider;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.item.gear.ItemHazmatSuitArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolRadiationShieldCapProvider implements IToolCapabilityProvider{

    public ToolRadiationShieldCapProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(capability == Capabilities.RADIATION_SHIELDING_CAPABILITY && tool.getModifierLevel(TicEXModuleProvider.MODIFIER_RADIATION_SHIELD.get()) > 0){
            return LazyOptional.of(()->RadiationShieldingHandler.create(item -> ItemHazmatSuitArmor.getShieldingByArmor(item.getEquipmentSlot()))).cast();
        }

        return LazyOptional.empty();
    }
    
}
