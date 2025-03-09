package moffy.ticex.modules;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.event.TicEXEvent;
import moffy.ticex.item.ItemReconstCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXModule extends AddonModule{

    public TicEXModule(){
        TicEXRegister.RECONSTRUCTION_CORE = TicEXRegister.ITEMS.register("reconstruction_core", ()->new ItemReconstCore(new Item.Properties(), null));

        TicEXRegister.HEALING_RECEIVED = TicEXRegister.ATTRIBUTES.register("healing_received", ()->new RangedAttribute("attribute."+TicEX.MODID+".healing_received", 1f, 0f, 1f));
        TicEXRegister.DAMAGE_TAKEN = TicEXRegister.ATTRIBUTES.register("damage_taken", ()->new RangedAttribute("attribute."+TicEX.MODID+".damage_taken", 1f, Float.MIN_NORMAL, 1f).setSyncable(true));

        TicEXRegister.CREATIVE_TAB = TicEXRegister.CREATIVE_TABS.register(TicEX.MODID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab."+TicEX.MODID))
                                   .icon(() -> new ItemStack(TicEXRegister.RECONSTRUCTION_CORE.get()))
                                   .displayItems(TicEXRegister::addTabItems)
                                   .build());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TicEXRegister.ITEMS.register(bus);
        TicEXRegister.BLOCKS.register(bus);
        TicEXRegister.FLUIDS.register(bus);
        TicEXRegister.MODIFIERS.register(bus);
        TicEXRegister.ATTRIBUTES.register(bus);
        TicEXRegister.CREATIVE_TABS.register(bus);
        
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHeal);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHurt);
    }
}
