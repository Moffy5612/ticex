package com.moffy5612.ticex.handlers.slashblade;

import com.moffy5612.addonlib.api.ContentHandlerBase;
import com.moffy5612.ticex.events.handlers.SlashBladeInteractionEventHandler;
import com.moffy5612.ticex.events.handlers.SummonedSwordEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
            MinecraftForge.EVENT_BUS.addListener(SummonedSwordEventHandler::onInputCommand);
            MinecraftForge.EVENT_BUS.addListener(SlashBladeInteractionEventHandler::onPlayerInteractEntity);
        }
    }

    @Override
    public void enqueueIMC(InterModEnqueueEvent event) {
        if(!isModsLoaded())return;
        TicEXSlashBladeSetup.onEnqueueIMC(event);
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct", "slashblade"};
    }
}
