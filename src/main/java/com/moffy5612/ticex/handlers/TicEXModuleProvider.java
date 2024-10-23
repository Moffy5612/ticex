package com.moffy5612.ticex.handlers;

import com.moffy5612.ticex.Reference;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class TicEXModuleProvider extends AbstractModifierProvider{
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Reference.MOD_ID);
        public static StaticModifier<Modifier> MODIFIER_EVOLVED = null;

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
