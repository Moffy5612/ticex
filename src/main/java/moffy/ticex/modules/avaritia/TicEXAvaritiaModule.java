package moffy.ticex.modules.avaritia;

import java.util.List;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierAftershock;
import moffy.ticex.modifier.ModifierBedrockBreaker;
import moffy.ticex.modifier.ModifierCelestial;
import moffy.ticex.modifier.ModifierCondensing;
import moffy.ticex.modifier.ModifierOmnipotence;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.utils.TicEXFluidUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

public class TicEXAvaritiaModule extends AddonModule{

    public TicEXAvaritiaModule(){
        TicEXRegistry.OMNIPOTEMCE_MODIFIER = TicEXRegistry.MODIFIERS.register("omnipotence", ModifierOmnipotence::new);
        TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_unbreakable");
        TicEXRegistry.COSMIC_LUCK_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_luck");
        TicEXRegistry.BEDROCK_BREAKER_MODIFIER = TicEXRegistry.MODIFIERS.register("bedrock_breaker", ModifierBedrockBreaker::new);
        TicEXRegistry.CELESTIAL_MODIFIER = TicEXRegistry.MODIFIERS.register("celestial", ModifierCelestial::new);
        TicEXRegistry.CONDENSING_MODIFIER = TicEXRegistry.MODIFIERS.register("condensing", ModifierCondensing::new);
        TicEXRegistry.AFTERSHOCK_MODIFIER = TicEXRegistry.MODIFIERS.register("aftershock", ModifierAftershock::new);

        TicEXRegistry.MOLTEN_INFINITY = TicEXRegistry.FLUIDS.register("molten_infinity").type(TicEXFluidUtil.hot("molten_infinity").temperature(6360).lightLevel(15)).block(BurningLiquidBlock.createBurning(MapColor.EMERALD, 15, 20, 20f)).bucket().commonTag().flowing();
        TicEXRegistry.MOLTEN_NEUTRONIUM = TicEXRegistry.FLUIDS.register("molten_neutronium").type(TicEXFluidUtil.cool().temperature(1000)).block(MapColor.COLOR_BLACK, 0).bucket().commonTag().flowing();        
        TicEXRegistry.MOLTEN_CRYSTAL_MATRIX = TicEXRegistry.FLUIDS.register("molten_crystal_matrix").type(TicEXFluidUtil.cool().temperature(1000)).block(MapColor.COLOR_LIGHT_BLUE, 0).bucket().commonTag().flowing();    
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        if(TierSortingRegistry.isTierSorted(InfinityTier.instance)){
            TierSortingRegistry.registerTier(InfinityTier.instance, new ResourceLocation(TicEX.MODID, "infinity"), List.of(TierSortingRegistry.getSortedTiers().get(TierSortingRegistry.getSortedTiers().size() - 1)), List.of());
        } else {
            TierSortingRegistry.registerTier(InfinityTier.instance, new ResourceLocation(TicEX.MODID, "infinity"), List.of(Tiers.NETHERITE), List.of());
        }
    }
}
