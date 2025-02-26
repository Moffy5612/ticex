package com.moffy5612.ticex.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.moffy5612.ticex.TicEXReference;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

public class TicEXRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeHelper{
    
    public TicEXRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @SuppressWarnings("null")
    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        
        String salvageFolder = "tools/modifiers/salvage/";
        String modifierFolder = "tools/modifiers/";

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.of(TinkerTags.Items.MELEE));
        ingredients.add(Ingredient.of(TinkerTags.Items.HARVEST));

        ModifierRecipeBuilder.modifier(TicEXModuleProvider.MODIFIER_EVOLVED)
            .setTools(Ingredient.merge(ingredients))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "draconium_core"))))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("minecraft", "golden_apple")),2))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("minecraft", "ender_eye")),2))
            .addInput(SizedIngredient.of(Ingredient.of(Items.GEMS_DIAMOND)))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "module_core"))))
            .setMaxLevel(1)
            .disallowCrystal()
            .setSlots(SlotType.ABILITY, 1)
            .setSalvageLevelRange(1, 4)
            .saveSalvage(consumer, prefix(TicEXModuleProvider.MODIFIER_EVOLVED.getId(), salvageFolder))
            .save(consumer, prefix(TicEXModuleProvider.MODIFIER_EVOLVED.getId(), modifierFolder+"_1"));

        ModifierRecipeBuilder.modifier(TicEXModuleProvider.MODIFIER_EVOLVED)
            .setTools(Ingredient.merge(ingredients))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "wyvern_core"))))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "draconium_core")),2))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("minecraft", "nether_star")),2))
            .addInput(SizedIngredient.of(Ingredient.of(Items.GEMS_EMERALD)))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "module_core"))))
            .setMaxLevel(2)
            .disallowCrystal()
            .setSlots(SlotType.ABILITY, 1)
            .save(consumer, prefix(TicEXModuleProvider.MODIFIER_EVOLVED.getId(), modifierFolder+"_2"));
        ModifierRecipeBuilder.modifier(TicEXModuleProvider.MODIFIER_EVOLVED)
            .setTools(Ingredient.merge(ingredients))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "awakened_core"))))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "wivern_core")),2))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("minecraft", "nether_star")),2))
            .addInput(SizedIngredient.of(Ingredient.of(Items.STORAGE_BLOCKS_EMERALD)))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "module_core"))))
            .setMaxLevel(3)
            .disallowCrystal()
            .setSlots(SlotType.ABILITY, 1)
            .save(consumer, prefix(TicEXModuleProvider.MODIFIER_EVOLVED.getId(), modifierFolder+"_3"));
        ModifierRecipeBuilder.modifier(TicEXModuleProvider.MODIFIER_EVOLVED)
            .setTools(Ingredient.merge(ingredients))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "chaotic_core"))))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "wivern_core")),2))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "awakened_core")),2))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("minecraft", "dragon_egg"))))
            .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("draconicevolution", "module_core"))))
            .setMaxLevel(4)
            .disallowCrystal()
            .setSlots(SlotType.ABILITY, 1)
            .save(consumer, prefix(TicEXModuleProvider.MODIFIER_EVOLVED.getId(), modifierFolder+"_4"));
    }

    @Override
    public String getModId() {
        return TicEXReference.MOD_ID;
    }

    @Override
        public String getName() {
            return "TicEX Recipes";
        }

    
}
