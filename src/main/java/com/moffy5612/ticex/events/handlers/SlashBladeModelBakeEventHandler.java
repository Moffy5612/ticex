package com.moffy5612.ticex.events.handlers;

import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladeItems;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SlashBladeModelBakeEventHandler {
    public static void onModelBake(final ModelBakeEvent event){
        ModelResourceLocation loc = new ModelResourceLocation(
                ForgeRegistries.ITEMS.getKey(TicEXSlashBladeItems.SLASHBLADE.asItem()), "inventory");
        event.getModelRegistry().put(loc, new BladeModel(event.getModelRegistry().get(loc), event.getModelLoader()));
    }
}
