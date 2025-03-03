package com.moffy5612.ticex.handlers.slashblade;

import com.moffy5612.ticex.events.TicEXSlashBladeEvent;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.modifiers.KoshiraeModifier;

import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class TicEXSlashBladeSetup {

    public static void setup(FMLCommonSetupEvent event){

        TicEXModuleProvider.MODIFIER_KOSHIRAE = TicEXModuleProvider.MODIFIERS.register("koshirae", KoshiraeModifier::new);
    }

    public static void onEnqueueIMC(InterModEnqueueEvent event){
        MinecraftForge.EVENT_BUS.unregister(SummonedSwordArts.getInstance());
    }
}
