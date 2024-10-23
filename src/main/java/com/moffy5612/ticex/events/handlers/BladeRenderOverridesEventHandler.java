package com.moffy5612.ticex.events.handlers;

import com.moffy5612.ticex.items.ToolSlashBlade;

import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;

public class BladeRenderOverridesEventHandler {
    public static void onItemOverrided(RenderOverrideEvent event){
        if(event.getStack().getItem() instanceof ToolSlashBlade){
            event.setCanceled(true);
        }
    }
}
