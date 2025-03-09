package moffy.ticex.modules;

import moffy.ticex.TicEX;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class TicEXRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEX.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TicEX.MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TicEX.MODID);
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEX.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TicEX.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TicEX.MODID);

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB = null;

    public static RegistryObject<Item> RECONSTRUCTION_CORE = null;

    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_INFINITY = null;

    public static RegistryObject<Attribute> HEALING_RECEIVED = null;
    public static RegistryObject<Attribute> DAMAGE_TAKEN = null;

    public static StaticModifier<Modifier> OMNIPOTEMCE_MODIFIER = null;
    public static DynamicModifier COSMIC_UNBREAKABLE_MODIFIER = null;
    public static DynamicModifier COSMIC_LUCK_MODIFIER = null;
    public static StaticModifier<Modifier> BEDROCK_BREAKER_MODIFIER = null;
    

    public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for(RegistryObject<Item> itemObject : ITEMS.getEntries()){
            output.accept(itemObject.get());
        }

        for(RegistryObject<Block> blockObject : BLOCKS.getEntries()){
            output.accept(blockObject.get().asItem());
        }
    }
}
