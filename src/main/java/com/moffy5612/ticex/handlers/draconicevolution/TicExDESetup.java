package com.moffy5612.ticex.handlers.draconicevolution;

import com.moffy5612.ticex.caps.evolved.ToolEvolvedCapProvider;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.modifiers.EvolvedModifierTool;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXDESetup {
    public static void run(FMLCommonSetupEvent event) {
        ToolCapabilityProvider.register(ToolEvolvedCapProvider::new);
        
        TicEXModuleProvider.MODIFIER_EVOLVED = TicEXModuleProvider.MODIFIERS.register("evolved", EvolvedModifierTool::new);
    }
}
