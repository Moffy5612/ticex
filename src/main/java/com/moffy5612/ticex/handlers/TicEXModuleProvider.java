package com.moffy5612.ticex.handlers;

import com.moffy5612.ticex.TicEXReference;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class TicEXModuleProvider extends AbstractModifierProvider{
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEXReference.MOD_ID);
        public static StaticModifier<Modifier> MODIFIER_EVOLVED = null;
        public static StaticModifier<Modifier> MODIFIER_KATARIZE = null;

        public TicEXModuleProvider(DataGenerator generator) {
            super(generator);
        }

        @Override
        public String getName() {
            return "TicEX Modifiers";
        }

        @Override
        protected void addModifiers() {
            
        }
}
