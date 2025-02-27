package com.moffy5612.ticex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.moffy5612.ticex.handlers.TicEXHandler;
import com.moffy5612.ticex.handlers.draconicevolution.TicEXDEHandler;
import com.moffy5612.ticex.handlers.mekanism.TicEXMekanismHandler;
import com.moffy5612.ticex.handlers.projecte.TicEXProjectEHandler;
import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladeHandler;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(TicEXReference.MOD_ID)
@Mod.EventBusSubscriber(modid = TicEXReference.MOD_ID, bus = Bus.MOD)
public class TicEX {
    private static final String PROTOCOL_VERSION = "1";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(TicEXReference.MOD_ID, "main"), 
        ()->PROTOCOL_VERSION, 
        PROTOCOL_VERSION::equals, 
        PROTOCOL_VERSION::equals
    );

    public static final TicEXHandler TICEX_HANDLER = new TicEXHandler();
    public static final TicEXDEHandler DE_HANDLER = new TicEXDEHandler();
    public static final TicEXSlashBladeHandler SLASH_BLADE_HANDLER = new TicEXSlashBladeHandler();
    public static final TicEXProjectEHandler PROJECT_E_HANDLER = new TicEXProjectEHandler();
    public static final TicEXMekanismHandler MEKANISM_HANDLER = new TicEXMekanismHandler();

    public TicEX () {
        TicEXConfig.register();

        TICEX_HANDLER.Handle();
        DE_HANDLER.Handle();
        SLASH_BLADE_HANDLER.Handle();
        PROJECT_E_HANDLER.Handle();
        MEKANISM_HANDLER.Handle();
    }
}
