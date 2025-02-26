package com.moffy5612.ticex.handlers.slashblade;

import java.util.function.*;

import com.moffy5612.ticex.TicEXReference;
import com.moffy5612.ticex.items.ToolSlashBlade;

import mods.flammpfeil.slashblade.item.ItemTierSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class TicEXSlashBladeItems{

    private static final ItemTierSlashBlade TIER = new ItemTierSlashBlade(()->{
        TagKey<Item> tags = ItemTags.create(new ResourceLocation("slashblade","proudsouls"));
        return Ingredient.of(tags);
    });
    private static final ToolDefinition SLASHBLADE_DEFINITION = ToolDefinition.builder(new ResourceLocation(TicEXReference.MOD_ID, "slashblade")).meleeHarvest().build();

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TicEXReference.MOD_ID);
    public static final ItemDeferredRegisterExtension ITEMS_EXTENDED = new ItemDeferredRegisterExtension(TicEXReference.MOD_ID);
    
    private static final Item.Properties SMELTERY_PROPS = new Item.Properties().tab(TinkerSmeltery.TAB_SMELTERY);

    private static final Item.Properties PARTS_PROPS = new Item.Properties().tab(TinkerToolParts.TAB_TOOL_PARTS);
    private static final Supplier<Item.Properties> TOOL_PROPS = () -> new Item.Properties().tab(TinkerTools.TAB_TOOLS);
    public static final ItemObject<ToolPartItem> BLADE = ITEMS_EXTENDED.register("slashblade_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
    public static final ItemObject<ToolPartItem> SAYA = ITEMS_EXTENDED.register("slashblade_saya", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
    public static final ItemObject<ToolSlashBlade> SLASHBLADE = ITEMS_EXTENDED.register("slashblade", () -> new ToolSlashBlade(TIER, 0, -2.4f, TOOL_PROPS.get(), SLASHBLADE_DEFINITION));
    public static final CastItemObject BLADE_CAST = ITEMS_EXTENDED.registerCast("slashblade_blade", SMELTERY_PROPS);
    public static final CastItemObject SAYA_CAST = ITEMS_EXTENDED.registerCast("slashblade_saya", SMELTERY_PROPS);

    public static final ItemObject<Item> SHEATH = ITEMS.register("sheath", new Item.Properties().stacksTo(1));
}
