package com.moffy5612.ticex;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class TicEXConfig {
    public static ForgeConfigSpec.BooleanValue INSTANT_KILL_ALL;

    public static void register() {
		registerCommonConfigs();
	}

    public static void registerCommonConfigs() {
        ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();

        COMMON.comment("Materialis Compat Options").push("materialis");
        INSTANT_KILL_ALL = COMMON.comment("When a tool with the Evolved modifier HITS an enemy that can only be damaged in a special way, should trigger InstaKill (true: yes)")
        .define("instaKillAll", true);

        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());
    }
}
