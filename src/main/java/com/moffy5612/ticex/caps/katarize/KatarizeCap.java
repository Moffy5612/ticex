package com.moffy5612.ticex.caps.katarize;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.moffy5612.ticex.modifiers.KatarizeModifier;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


public class KatarizeCap implements IItemCharge, IItemMode, IExtraFunction{
	private static final Predicate<Entity> SLAY_MOB;
   	private static final Predicate<Entity> SLAY_ALL;

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		if (player.getAttackStrengthScale(0.0F) == 1.0F) {// 153
         attackAOE(stack, player, this.getMode(stack) == 1, ProjectEConfig.server.difficulty.katarDeathAura.get(), 0L, hand);// 154
         PlayerHelper.resetCooldown(player);
         return true;
      } else {
         return false;
      }
	}
	
	@Override
	public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		int currentCharge = this.getCharge(stack);
	      int numCharges = this.getNumCharges(stack);
	      if (player.isShiftKeyDown()) {
	         if (currentCharge > 0) {
		            player.level
		               .playSound(
		                  null,
		                  player.getX(),
		                  player.getY(),
		                  player.getZ(),
		                  PESounds.UNCHARGE,
		                  SoundSource.PLAYERS,
		                  1.0F,
		                  0.5F + 0.5F / (float)numCharges * (float)currentCharge
		               );
		            CompoundTag tag = ToolStack.from(stack).getPersistentData().getCompound(KatarizeModifier.KATARIZE_LOCATION);
			        tag.putInt("Charge", currentCharge - 1);
			        ToolStack.from(stack).getPersistentData().put(KatarizeModifier.KATARIZE_LOCATION, tag);
		            return true;
	         }
	     } else if (currentCharge < numCharges) {
	         player.level
	            .playSound(
	               null,
	               player.getX(),
	               player.getY(),
	               player.getZ(),
	               PESounds.CHARGE,
	               SoundSource.PLAYERS,
	               1.0F,
	               0.5F + 0.5F / (float)numCharges * (float)currentCharge
	            );
	         CompoundTag tag = ToolStack.from(stack).getPersistentData().getCompound(KatarizeModifier.KATARIZE_LOCATION);
	         tag.putInt("Charge", currentCharge + 1);
	         ToolStack.from(stack).getPersistentData().put(KatarizeModifier.KATARIZE_LOCATION, tag);
	         return true;
	      }
	      return false;
	}
	
	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		byte numModes = this.getModeCount();
	      if (numModes < 2) {
	         return false;
	      } else {
	    	 CompoundTag tag = ToolStack.from(stack).getPersistentData().getCompound(KatarizeModifier.KATARIZE_LOCATION);
	         tag.putByte("mode", (byte)((this.getMode(stack) + 1) % numModes));
	         ToolStack.from(stack).getPersistentData().put(KatarizeModifier.KATARIZE_LOCATION, tag);
	         player.sendMessage(this.getModeSwitchEntry().translate(new Object[]{this.getModeLangEntry(stack)}), Util.NIL_UUID);// 46
	         return true;
	      }
	}
	
	@Override
	public int getCharge(@NotNull ItemStack stack) {
		return ToolStack.from(stack).getPersistentData().getCompound(KatarizeModifier.KATARIZE_LOCATION).getInt("Charge");
	}
	
	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ToolStack.from(stack).getPersistentData().getCompound(KatarizeModifier.KATARIZE_LOCATION).getByte("Mode");
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return new ILangEntry[] {PELang.MODE_KATAR_1, PELang.MODE_KATAR_2};
	}

	@Override
	public int getNumCharges(@NotNull ItemStack var1) {
		return 4;
	}

	private void attackAOE(ItemStack stack, Player player, boolean slayAll, float damage, long emcCost, InteractionHand hand) {
      Level level = player.getCommandSenderWorld();
      if (!level.isClientSide) {
         int charge = getCharge(stack);
         List<Entity> toAttack = level.getEntities(player, player.getBoundingBox().inflate((double)(2.5F * (float)charge)), slayAll ? SLAY_ALL : SLAY_MOB);
         DamageSource src = DamageSource.playerAttack(player).bypassArmor();
         boolean hasAction = false;
		 ToolStack tool = ToolStack.from(stack);

         for(Iterator<Entity> var12 = toAttack.iterator(); var12.hasNext(); hasAction = true) {
            Entity entity = (Entity)var12.next();
            if (!ItemPE.consumeFuel(player, stack, emcCost, true)) {
               break;
            }
			
            entity.hurt(src, damage / 20 * ToolAttackUtil.getAttributeAttackDamage(tool, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
         }

         if (hasAction) {
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), (SoundEvent)PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            PlayerHelper.swingItem(player, hand);
         }

      }
   	}

	static{
		SLAY_MOB = (entity) -> {
         return !entity.isSpectator() && (entity instanceof Enemy || entity instanceof PartEntity);
		};
		SLAY_ALL = (entity) -> {
			return !entity.isSpectator() && (entity instanceof Enemy || entity instanceof LivingEntity || entity instanceof PartEntity);
		};
	}
}
