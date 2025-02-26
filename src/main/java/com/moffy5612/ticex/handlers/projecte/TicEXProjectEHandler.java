package com.moffy5612.ticex.handlers.projecte;

import com.moffy5612.addonlib.api.ContentHandlerBase;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TicEXProjectEHandler extends ContentHandlerBase{

    @Override
    public void setup(FMLCommonSetupEvent event) {
        if(isModsLoaded()){
            TicEXProjectESetup.setup(event);
        }
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "projecte"};
    }
    
}
