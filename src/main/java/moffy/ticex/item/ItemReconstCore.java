package moffy.ticex.item;

import java.util.List;

import moffy.ticex.TicEX;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemReconstCore extends Item{

    private String modifierName;

    public ItemReconstCore(Properties properties, String modifierName) {
        super(properties);
        this.modifierName = modifierName;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return modifierName != null;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if(modifierName == null){
            components.add(Component.translatable("item." + TicEX.MODID + ".reconstruction_core.desc"));
        } else {
            components.add(Component.translatable("modifier." + TicEX.MODID + "." + modifierName));
        }
    }
}
