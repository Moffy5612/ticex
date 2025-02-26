package com.moffy5612.ticex.modifiers;

import com.moffy5612.ticex.TicEXReference;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.impl.DurabilityShieldModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class KatarizeModifier extends DurabilityShieldModifier{
	
	public static final ResourceLocation KATARIZE_LOCATION = new ResourceLocation(TicEXReference.MOD_ID, "katarize");

	@Override
	protected int getShieldCapacity(IToolStackView arg0, int arg1) {
		return 4;
	}

	@Override
	public Boolean showDurabilityBar(IToolStackView tool, int level) {
		return true;
	}
	
	@Override
    protected ResourceLocation getShieldKey() {
        return getId();
    }
	
	@Override
	protected int getShield(IToolStackView tool) {
		return tool.getPersistentData().getCompound(KATARIZE_LOCATION).getInt("Charge");
	}
	
	@Override
	public int getDurabilityRGB(IToolStackView tool, int level) {
		return 0x32a852;
	}
}
