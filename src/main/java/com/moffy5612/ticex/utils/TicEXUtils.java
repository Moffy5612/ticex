package com.moffy5612.ticex.utils;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXUtils {

    public static Modifier getModifier(ModifierNBT modifiers, Modifier target){
        ModifierEntry entry = modifiers.getEntry(target.getId());
        if(entry != null){
            return entry.getModifier();
        }
        return null;
    }

    public static int longToIntWithPercentage(long value){
        return (int)Math.round((double)value / Long.MAX_VALUE * Integer.MAX_VALUE * 1000);
    }

    public static Entity getEntityClosestTo(Entity from){
        Entity result = null;
        float minDistance = Float.MAX_VALUE;
        for (Entity entity : from.level.getEntitiesOfClass(Entity.class, from.getBoundingBox().inflate(32D, 0.25D, 32D))) {
            float distance = from.distanceTo(entity);
            if(distance < minDistance && !(entity.equals(from))){
                result = entity;
                minDistance = distance;
            }
        }
        return result;
    }

    private static boolean removeBlock(IToolStackView tool, ToolHarvestContext context, boolean isExtra) {
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
            state.getBlock().destroy(world, pos, state);
        }
        return removed;
    }

    public static boolean breakBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context){
        return breakBlock(tool, stack, context, false);
    }

    public static boolean breakExtraBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context){
        return breakBlock(tool, stack, context, true);
    }

    private static boolean breakBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context, boolean isExtra) {
		ServerPlayer player = Objects.requireNonNull(context.getPlayer());
		ServerLevel world = context.getWorld();
		BlockPos pos = context.getPos();
		GameType type = player.gameMode.getGameModeForPlayer();
		int exp = ForgeHooks.onBlockBreakEvent(world, type, player, pos);
		if (exp == -1) {
			return false;
		}

		if (player.isCreative()) {
			removeBlock(tool, context, isExtra);
			return true;
		}

		BlockState state = context.getState();
		int damage = ToolHarvestLogic.getDamage(tool, world, pos, state);

		BlockEntity te = world.getBlockEntity(pos);
		boolean removed = removeBlock(tool, context, context.canHarvest() || isExtra);

		Block block = state.getBlock();
		if (removed) {
			block.playerDestroy(world, player, pos, state, te, stack);
		}

		if (removed && exp > 0) {
			block.popExperience(world, pos, exp);
		}

		if (!tool.isBroken()) {
			for (ModifierEntry entry : tool.getModifierList()) {
				entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
			}
			ToolDamageUtil.damageAnimated(tool, damage, player);
		}

		return true;
	}
}
