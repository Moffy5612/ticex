package com.moffy5612.ticex.items;

import java.util.List;

import com.moffy5612.ticex.TicEXReference;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemReconstCore extends Item{

    private final String DEFAULT_TOOLTIP = "item." + TicEXReference.MOD_ID + ".reconstruction_core.desc";

    public ItemReconstCore(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, components, tooltipFlag);
        String type = stack.getOrCreateTag().getString("Type");
        if(type.equals(DEFAULT_TOOLTIP)){
            components.add(new TranslatableComponent(type).withStyle(ChatFormatting.GRAY));
        } else if(!type.equals("")){
            components.add(new TranslatableComponent(type).withStyle(ChatFormatting.AQUA));
        }
    }


    @Override
    public boolean isFoil(ItemStack stack) {
        String type = stack.getOrCreateTag().getString("Type");
        return !type.equals(DEFAULT_TOOLTIP) && !type.equals("");
    }
}