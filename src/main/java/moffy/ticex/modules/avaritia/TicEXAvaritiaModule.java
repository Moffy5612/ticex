package moffy.ticex.modules.avaritia;

import java.util.List;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierBedrockBreaker;
import moffy.ticex.modifier.ModifierOmnipotence;
import moffy.ticex.modules.TicEXRegister;
import moffy.ticex.utils.TicEXFluidUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

public class TicEXAvaritiaModule extends AddonModule{

    public TicEXAvaritiaModule(){
        TicEXRegister.OMNIPOTEMCE_MODIFIER = TicEXRegister.MODIFIERS.register("omnipotence", ModifierOmnipotence::new);
        TicEXRegister.COSMIC_UNBREAKABLE_MODIFIER = TicEXRegister.MODIFIERS.registerDynamic("cosmic_unbreakable");
        TicEXRegister.COSMIC_LUCK_MODIFIER = TicEXRegister.MODIFIERS.registerDynamic("cosmic_luck");
        TicEXRegister.BEDROCK_BREAKER_MODIFIER = TicEXRegister.MODIFIERS.register("bedrock_breaker", ModifierBedrockBreaker::new);

        TicEXRegister.MOLTEN_INFINITY = TicEXRegister.FLUIDS.register("molten_infinity").type(TicEXFluidUtil.hot("molten_infinity").temperature(6360).lightLevel(15)).block(BurningLiquidBlock.createBurning(MapColor.EMERALD, 15, 20, 20f)).bucket().commonTag().flowing();
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
