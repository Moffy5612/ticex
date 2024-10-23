package com.moffy5612.ticex.handlers.slashblade;

import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class TicEXSlashBladeSetup {
    public static void onEnqueueIMC(InterModEnqueueEvent event){
        MinecraftForge.EVENT_BUS.unregister(SummonedSwordArts.getInstance());
    }
}
