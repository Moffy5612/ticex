package com.moffy5612.ticex.utils;

import java.util.Objects;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.equipment.DETier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXDEUtils {
    public static DETier getTier(TechLevel techLevel){
        if(techLevel == TechLevel.DRACONIC) return DEContent.DRACONIC_TIER;
        else if(techLevel == TechLevel.CHAOTIC) return DEContent.CHAOTIC_TIER;
        else return DEContent.WYVERN_TIER;
    }

    private static boolean removeBlock(IToolStackView tool, ToolHarvestContext context, InventoryDynamic inventory, int xp, boolean isExtra) {
        Boolean removed = null;
        if (!tool.isBroken()) {
            for (ModifierEntry entry : tool.getModifierList()) {
                removed = entry.getModifier().removeBlock(tool, entry.getLevel(), context);
                if (removed != null) {
                    break;
                }
            }
        }
        BlockState state = context.getState();
        ServerLevel world = context.getWorld();
        BlockPos pos = context.getPos();
        if (removed == null) {
            removed = state.onDestroyedByPlayer(world, pos, context.getPlayer(), isExtra, world.getFluidState(pos));
        }
        if (removed) {
            BlockToStackHelper.breakAndCollectWithPlayer(world, pos, inventory, context.getPlayer(), xp);
            state.getBlock().destroy(world, pos, state);
        }
        return removed;
    }

    public static boolean breakBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context, InventoryDynamic inventory){
        return breakBlock(tool, stack, context, inventory, false);
    }

    public static boolean breakExtraBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context, InventoryDynamic inventory){
        return breakBlock(tool, stack, context, inventory, true);
    }

    private static boolean breakBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context, InventoryDynamic inventory, boolean isExtra) {
		ServerPlayer player = Objects.requireNonNull(context.getPlayer());
		ServerLevel world = context.getWorld();
		BlockPos pos = context.getPos();
		GameType type = player.gameMode.getGameModeForPlayer();
		int exp = ForgeHooks.onBlockBreakEvent(world, type, player, pos);
		if (exp == -1) {
			return false;
		}

		if (player.isCreative()) {
			removeBlock(tool, context, inventory, exp, isExtra);
			return true;
		}

		BlockState state = context.getState();
		int damage = ToolHarvestLogic.getDamage(tool, world, pos, state);

		BlockEntity te = world.getBlockEntity(pos);
		boolean removed = removeBlock(tool, context, inventory, exp, context.canHarvest() || isExtra);

		Block block = state.getBlock();
		if (removed) {
			block.playerDestroy(world, player, pos, state, te, stack);
		}

		if (!tool.isBroken()) {
			for (ModifierEntry entry : tool.getModifierList()) {
				if(!isExtra)entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
			}
			ToolDamageUtil.damageAnimated(tool, damage, player);
		}

		return true;
	}
}
