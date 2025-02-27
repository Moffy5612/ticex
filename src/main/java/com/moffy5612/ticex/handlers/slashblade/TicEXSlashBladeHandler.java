package com.moffy5612.ticex.handlers.slashblade;

import com.moffy5612.addonlib.api.ContentHandlerBase;
import com.moffy5612.ticex.events.TicEXSlashBladeEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXSlashBladeHandler extends ContentHandlerBase{

    public TicEXSlashBladeHandler(){
        super();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if(isModsLoaded()){
            TicEXSlashBladeItems.ITEMS.register(bus);
            TicEXSlashBladeItems.ITEMS_EXTENDED.register(bus);
            TicEXSlashBladeEntities.ENTITIES.register(bus);
            MinecraftForge.EVENT_BUS.addListener(TicEXSlashBladeEvent::onInputCommand);
            MinecraftForge.EVENT_BUS.addListener(TicEXSlashBladeEvent::onPlayerInteractEntity);
        }
    }

    @Override
    public void enqueueIMC(InterModEnqueueEvent event) {
        if(!isModsLoaded())return;
        TicEXSlashBladeSetup.onEnqueueIMC(event);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if(isModsLoaded()){
            bus.addListener(TicEXSlashBladeEvent::onModelBake);
            MinecraftForge.EVENT_BUS.addListener(TicEXSlashBladeEvent::onItemOverrided);
        } 
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "slashblade"};
    }
}
