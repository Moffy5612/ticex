package com.moffy5612.ticex.handlers.slashblade;

import com.moffy5612.ticex.client.renderer.TicEXLayerMainBlade;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class TicEXSlashBladePlayerMixin {
    public static void handle(PlayerRenderer renderer){
        renderer.addLayer(new TicEXLayerMainBlade(renderer));
    }
}
