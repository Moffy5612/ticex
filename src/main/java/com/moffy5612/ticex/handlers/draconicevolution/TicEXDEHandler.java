package com.moffy5612.ticex.handlers.draconicevolution;

import com.moffy5612.addonlib.api.ContentHandlerBase;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXDEHandler extends ContentHandlerBase{
    public TicEXDEHandler(){
        super();
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if(isModsLoaded())TicEXModuleProvider.MODIFIERS.register(bus);
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        if(!isModsLoaded())return;

        TicExDESetup.run(event);
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "draconicevolution"};
        
    }
}
