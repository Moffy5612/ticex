package com.moffy5612.ticex.events.handlers;

import com.moffy5612.ticex.Reference;
import com.moffy5612.ticex.TicEX;
import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladeItems;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SlashBladeModelBakeEventHandler {

    @SubscribeEvent
    public static void onModelBake(final ModelBakeEvent event){
        if(!TicEX.SLASH_BLADE_HANDLER.isModsLoaded())return;
        ModelResourceLocation loc = new ModelResourceLocation(
                ForgeRegistries.ITEMS.getKey(TicEXSlashBladeItems.SLASHBLADE.asItem()), "inventory");
        event.getModelRegistry().put(loc, new BladeModel(event.getModelRegistry().get(loc), event.getModelLoader()));
    }
}
