package com.moffy5612.ticex.handlers.mekanism;

import com.moffy5612.ticex.caps.radiation_shield.ToolRadiationShieldCapProvider;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.modifiers.RadiationShieldModifier;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXMekanismSetup {
    public static void setup(FMLCommonSetupEvent event){
        ToolCapabilityProvider.register(ToolRadiationShieldCapProvider::new);

        TicEXModuleProvider.MODIFIER_RADIATION_SHIELD = TicEXModuleProvider.MODIFIERS.register("radiation_shield", RadiationShieldModifier::new);
    }
}
