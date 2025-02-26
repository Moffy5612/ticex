package com.moffy5612.ticex.modifiers;

import java.util.List;

import com.moffy5612.ticex.TicEXReference;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class KatarizeModifier extends NoLevelsModifier{
	public static final ResourceLocation KATARIZE_LOCATION = new ResourceLocation(TicEXReference.MOD_ID, "katarize");

    @Override
    public void addInformation(IToolStackView tool, int level, Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        super.addInformation(tool, level, player, tooltip, tooltipKey, tooltipFlag);
        tooltip.add(new TranslatableComponent("modifier."+TicEXReference.MOD_ID+".katarize.charge").append(": "+tool.getPersistentData().getCompound(KATARIZE_LOCATION).getInt("Charge") + " / 4"));
    }
}
