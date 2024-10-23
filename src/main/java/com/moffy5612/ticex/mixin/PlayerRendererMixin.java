package com.moffy5612.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moffy5612.ticex.TicEX;
import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladePlayerMixin;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(
      at = {@At("TAIL")},
      method = {"<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V"}
   )
   public void extraLayers(EntityRendererProvider.Context context, boolean isSlim, CallbackInfo callback) {
        if(!TicEX.SLASH_BLADE_HANDLER.isModsLoaded())return;
        TicEXSlashBladePlayerMixin.handle((PlayerRenderer)((Object)this));
   }
}
