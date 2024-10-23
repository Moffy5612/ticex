package com.moffy5612.ticex.integration.materialis;

import com.rcx.materialis.datagen.MaterialisModifiers;

import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class MaterialisModifierUtils {
    public static boolean hasInstaKill(ItemStack stack){
        return ToolStack.from(stack).getModifierLevel(MaterialisModifiers.instakillModifier.get()) > 0;
    }

    public static boolean hasInstaMine(ItemStack stack){
        return ToolStack.from(stack).getModifierLevel(MaterialisModifiers.instamineModifier.get()) > 0;
    }
}
