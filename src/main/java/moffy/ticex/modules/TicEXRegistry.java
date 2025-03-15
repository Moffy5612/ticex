package moffy.ticex.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import moffy.ticex.TicEX;
import moffy.ticex.block.entity.RFFurnaceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

public class TicEXRegistry {

    public static final BlockBehaviour.Properties SEARED;
    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
        builder(MapColor.COLOR_GRAY, SoundType.METAL)
            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F * factor, 9.0F * factor)
            .isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
        SEARED = solidProps.apply(1);
    }

    public static final BlockBehaviour.Properties SCORCHED;
    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor -> builder(MapColor.TERRACOTTA_BROWN, SoundType.BASALT)
        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.5F * factor, 8.0F * factor)
        .isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
        SCORCHED = solidProps.apply(1);
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEX.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TicEX.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TicEX.MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TicEX.MODID);
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEX.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TicEX.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TicEX.MODID);

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB = null;

    public static RegistryObject<Item> RECONSTRUCTION_CORE = null;

    public static RegistryObject<Block> SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SCORCHED_RF_FURNACE = null;
    
    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> RF_FURNACE_ENTITY = null;
    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> CREATICE_RF_FURNACE_ENTITY = null;

    public static FluidObject<UnplaceableFluid> MOLTEN_RECONSTRUCTION_CORE = null;
    public static List<FluidObject<UnplaceableFluid>> RF_FURNACE_FUELS = new ArrayList<>(); 
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_INFINITY = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_NEUTRONIUM = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_CRYSTAL_MATRIX = null;

    public static RegistryObject<Attribute> HEALING_RECEIVED = null;
    public static RegistryObject<Attribute> DAMAGE_TAKEN = null;

    public static StaticModifier<Modifier> OMNIPOTEMCE_MODIFIER = null;
    public static DynamicModifier COSMIC_UNBREAKABLE_MODIFIER = null;
    public static DynamicModifier COSMIC_LUCK_MODIFIER = null;
    public static StaticModifier<Modifier> BEDROCK_BREAKER_MODIFIER = null;
    public static StaticModifier<Modifier> CELESTIAL_MODIFIER = null;
    public static StaticModifier<Modifier> CONDENSING_MODIFIER = null;
    public static StaticModifier<Modifier> AFTERSHOCK_MODIFIER = null;

    public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for(RegistryObject<Item> itemObject : ITEMS.getEntries()){
            output.accept(itemObject.get());
        }

        for(RegistryObject<Block> blockObject : BLOCKS.getEntries()){
            output.accept(blockObject.get().asItem());
        }
    }

    private static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
    return BlockBehaviour.Properties.of().sound(soundType).mapColor(color);
  }
}
