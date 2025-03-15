package moffy.ticex.modules;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.block.RFFurnaceBlock;
import moffy.ticex.block.entity.RFFurnaceBlockEntity;
import moffy.ticex.event.TicEXEvent;
import moffy.ticex.item.ItemReconstCore;
import moffy.ticex.utils.TicEXFluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXModule extends AddonModule{

    public TicEXModule(){
        TicEXRegistry.RECONSTRUCTION_CORE = TicEXRegistry.ITEMS.register("reconstruction_core", ()->new ItemReconstCore(new Item.Properties(), null));

        TicEXRegistry.SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("seared_rf_furnace", () -> new RFFurnaceBlock(TicEXRegistry.SEARED, false));
        TicEXRegistry.SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("scorched_rf_furnace", () -> new RFFurnaceBlock(TicEXRegistry.SCORCHED, false));
        TicEXRegistry.CREATIVE_SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_seared_rf_furnace", () -> new RFFurnaceBlock(TicEXRegistry.SEARED, true));
        TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_scorched_rf_furnace", () -> new RFFurnaceBlock(TicEXRegistry.SCORCHED, true));
        TicEXRegistry.ITEMS.register("seared_rf_furnace", ()->new BlockItem(TicEXRegistry.SEARED_RF_FURNACE.get(), new Item.Properties()));
        TicEXRegistry.ITEMS.register("scorched_rf_furnace", ()->new BlockItem(TicEXRegistry.SCORCHED_RF_FURNACE.get(), new Item.Properties()));
        TicEXRegistry.ITEMS.register("creative_seared_rf_furnace", ()->new BlockItem(TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(), new Item.Properties()));
        TicEXRegistry.ITEMS.register("creative_scorched_rf_furnace", ()->new BlockItem(TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get(), new Item.Properties()));

        TicEXRegistry.RF_FURNACE_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register(
            "rf_furnace_entity", 
            ()->BlockEntityType.Builder.of(
                (BlockPos pPos, BlockState pState)->new RFFurnaceBlockEntity(
                    TicEXRegistry.RF_FURNACE_ENTITY.get(), pPos, pState, false), 
                    TicEXRegistry.SEARED_RF_FURNACE.get(), 
                    TicEXRegistry.SCORCHED_RF_FURNACE.get(),
                    TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(), 
                    TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get()
                )
                .build(null)
            );

        TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE = TicEXRegistry.FLUIDS.register("molten_reconstruction_core").type(TicEXFluidUtil.slime("reconstruction_core").temperature(1000).density(-1600)).bucket().unplacable();
        for(int i = 0; i < 20; i++){
            TicEXRegistry.RF_FURNACE_FUELS.add(TicEXRegistry.FLUIDS.register("rf_furnace_fuel_"+i).type(TicEXFluidUtil.hot("rf_furnace_fuel_"+i).temperature(1000).density(-1600)).unplacable());
        }

        TicEXRegistry.HEALING_RECEIVED = TicEXRegistry.ATTRIBUTES.register("healing_received", ()->new RangedAttribute("attribute."+TicEX.MODID+".healing_received", 1f, 0f, 1f));
        TicEXRegistry.DAMAGE_TAKEN = TicEXRegistry.ATTRIBUTES.register("damage_taken", ()->new RangedAttribute("attribute."+TicEX.MODID+".damage_taken", 1f, Float.MIN_NORMAL, 1f).setSyncable(true));

        TicEXRegistry.CREATIVE_TAB = TicEXRegistry.CREATIVE_TABS.register(TicEX.MODID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab."+TicEX.MODID))
                                   .icon(() -> new ItemStack(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                                   .displayItems(TicEXRegistry::addTabItems)
                                   .build());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TicEXRegistry.ITEMS.register(bus);
        TicEXRegistry.BLOCKS.register(bus);
        TicEXRegistry.BLOCK_ENTITIES.register(bus);
        TicEXRegistry.FLUIDS.register(bus);
        TicEXRegistry.MODIFIERS.register(bus);
        TicEXRegistry.ATTRIBUTES.register(bus);
        TicEXRegistry.CREATIVE_TABS.register(bus);
        
        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHeal);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHurt);
    }
}
