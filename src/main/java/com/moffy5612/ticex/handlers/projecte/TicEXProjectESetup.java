package com.moffy5612.ticex.handlers.projecte;


import com.moffy5612.ticex.caps.katarize.ToolKatarizeCapProvider;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.modifiers.KatarizeModifier;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXProjectESetup {
    public static void setup(FMLCommonSetupEvent event){
        ToolCapabilityProvider.register(ToolKatarizeCapProvider::new);
        
        TicEXModuleProvider.MODIFIER_KATARIZE=TicEXModuleProvider.MODIFIERS.register("katarize", KatarizeModifier::new);
    }
}
