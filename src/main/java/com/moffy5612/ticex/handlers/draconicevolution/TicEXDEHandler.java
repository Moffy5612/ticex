package com.moffy5612.ticex.handlers.draconicevolution;

import com.moffy5612.addonlib.api.ContentHandlerBase;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TicEXDEHandler extends ContentHandlerBase{
    public TicEXDEHandler(){
        super();
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        if(!isModsLoaded())return;

        TicEXDESetup.run(event);
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "draconicevolution"};
        
    }
}
