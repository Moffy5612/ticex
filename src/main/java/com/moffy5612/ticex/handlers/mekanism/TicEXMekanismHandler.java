package com.moffy5612.ticex.handlers.mekanism;

import com.moffy5612.addonlib.api.ContentHandlerBase;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TicEXMekanismHandler extends ContentHandlerBase{

    @Override
    public void setup(FMLCommonSetupEvent event) {
        if(!isModsLoaded())return;
        TicEXMekanismSetup.setup(event);
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "mekanism"};
    }
}
