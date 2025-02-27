package com.moffy5612.ticex.handlers;

import com.moffy5612.addonlib.api.ContentHandlerBase;
import com.moffy5612.ticex.TicEXReference;
import com.moffy5612.ticex.items.ItemReconstCore;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TicEXHandler extends ContentHandlerBase{

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEXReference.MOD_ID);

    public static final RegistryObject<Item> RECONSTRUCTION_CORE = ITEMS.register("reconstruction_core", ()->new ItemReconstCore(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));    

    public TicEXHandler(){
        super();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        TicEXModuleProvider.MODIFIERS.register(bus);
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"tconstruct"};
    }
    
}
