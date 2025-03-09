package moffy.ticex;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.modules.TicEXModuleProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

@Mod(TicEX.MODID)
public class TicEX {
    public static final String MODID = "ticex";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TicEX(){
        ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(), COMMON);
        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());
    }
}
