package com.moffy5612.ticex.events.handlers;

import java.util.Optional;
import java.util.UUID;

import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladeItems;
import com.moffy5612.ticex.items.ToolSlashBlade;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateCapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.ComboState;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.capability.slashblade.RangeAttack;
import mods.flammpfeil.slashblade.capability.slashblade.combo.Extra;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class SlashBladeInteractionEventHandler {
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event){
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        if(target instanceof BladeStandEntity && stack.getItem().equals(Items.PAPER)){
            event.setCanceled(true);
            BladeStandEntity bladeStand = (BladeStandEntity)target;
            ItemStack slashbladeStack = bladeStand.getItem();
            Entity playerEntity = event.getEntity();
            if(!(slashbladeStack.getItem() instanceof ToolSlashBlade) && playerEntity instanceof Player){
                Player player = (Player)playerEntity;
                LazyOptional<ISlashBladeState> state = slashbladeStack.getCapability(BladeStateCapabilityProvider.CAP);
                ItemStack sheath = new ItemStack(TicEXSlashBladeItems.SHEATH.get());
                sheath.setTag(serializeNBT(state));
                stack.shrink(1);
                player.getInventory().add(sheath);
            }
        } else if (target instanceof BladeStandEntity && stack.getItem().equals(TicEXSlashBladeItems.SHEATH.get())){
            event.setCanceled(true);
            BladeStandEntity bladeStand = (BladeStandEntity)target;
            ItemStack slashbladeStack = bladeStand.getItem();
            Entity playerEntity = event.getEntity();
            if((slashbladeStack.getItem() instanceof ToolSlashBlade) && playerEntity instanceof Player){
                LazyOptional<ISlashBladeState> state = slashbladeStack.getCapability(BladeStateCapabilityProvider.CAP);
                CompoundTag nbt = stack.getTag();
                if(nbt != null){
                    deserializeNBT(state, nbt);
                    stack.shrink(1);
                }
            }
        }
    }

    private static CompoundTag serializeNBT(LazyOptional<ISlashBladeState> state){
        CompoundTag tag = new CompoundTag();
        state.ifPresent((instance) -> {
         tag.putLong("lastActionTime", instance.getLastActionTime());
         tag.putInt("TargetEntity", instance.getTargetEntityId());
         tag.putBoolean("_onClick", instance.onClick());
         tag.putFloat("fallDecreaseRate", instance.getFallDecreaseRate());
         tag.putBoolean("isCharged", instance.isCharged());
         tag.putFloat("AttackAmplifier", instance.getAttackAmplifier());
         tag.putString("currentCombo", instance.getComboSeq().getName());
         tag.putString("lastPosHash", instance.getLastPosHash());
         tag.putBoolean("HasShield", instance.hasShield());
         tag.putFloat("Damage", instance.getDamage());
         tag.putBoolean("isBroken", instance.isBroken());
         tag.putBoolean("isNoScabbard", instance.isNoScabbard());
         tag.putBoolean("isSealed", instance.isSealed());
         tag.putFloat("baseAttackModifier", instance.getBaseAttackModifier());
         tag.putInt("killCount", instance.getKillCount());
         tag.putInt("RepairCounter", instance.getRefine());
         UUID id = instance.getOwner();
         if (id != null) {
            tag.putUUID("Owner", id);
         }

         UUID bladeId = instance.getUniqueId();
         tag.putUUID("BladeUniqueId", bladeId);
         tag.putString("RangeAttackType", instance.getRangeAttackType().getName());
         tag.putString("SpecialAttackType", (String)Optional.ofNullable(instance.getSlashArtsKey()).orElse("none"));
         tag.putBoolean("isDestructable", instance.isDestructable());
         tag.putBoolean("isDefaultBewitched", instance.isDefaultBewitched());
         tag.putByte("rarityType", (byte)instance.getRarity().ordinal());
         tag.putString("translationKey", instance.getTranslationKey());
         tag.putByte("StandbyRenderType", (byte)instance.getCarryType().ordinal());
         tag.putInt("SummonedSwordColor", instance.getColorCode());
         tag.putBoolean("SummonedSwordColorInverse", instance.isEffectColorInverse());
         tag.put("adjustXYZ", NBTHelper.newDoubleNBTList(instance.getAdjust()));
         instance.getTexture().ifPresent((loc) -> {
            tag.putString("TextureName", loc.toString());
         });
         instance.getModel().ifPresent((loc) -> {
            tag.putString("ModelName", loc.toString());
         });
         tag.putString("ComboRoot", (String)Optional.ofNullable(instance.getComboRoot()).map((c) -> {
            return c.getName();
         }).orElseGet(() -> {
            return Extra.STANDBY_EX.getName();
         }));
         tag.putString("ComboRootAir", (String)Optional.ofNullable(instance.getComboRoot()).map((c) -> {
            return c.getName();
         }).orElseGet(() -> {
            return Extra.STANDBY_INAIR.getName();
         }));
      });
      return tag;
    }

    @SuppressWarnings("null")
    private static void deserializeNBT(LazyOptional<ISlashBladeState> state, CompoundTag nbt){
        Tag baseTag;
        if (nbt.contains("State")) {
            baseTag = nbt.get("State");
        } else {
            baseTag = nbt;
        }
    
        state.ifPresent((instance) -> {
            CompoundTag tag = (CompoundTag)baseTag;
            instance.setLastActionTime(tag.getLong("lastActionTime"));
            instance.setTargetEntityId(tag.getInt("TargetEntity"));
            instance.setOnClick(tag.getBoolean("_onClick"));
            instance.setFallDecreaseRate(tag.getFloat("fallDecreaseRate"));
            instance.setCharged(tag.getBoolean("isCharged"));
            instance.setAttackAmplifier(tag.getFloat("AttackAmplifier"));
            instance.setComboSeq((ComboState)ComboState.NONE.valueOf(tag.getString("currentCombo")));
            instance.setLastPosHash(tag.getString("lastPosHash"));
            instance.setHasShield(tag.getBoolean("HasShield"));
            instance.setDamage(tag.getFloat("Damage"));
            instance.setBroken(tag.getBoolean("isBroken"));
            instance.setHasChangedActiveState(true);
            instance.setNoScabbard(tag.getBoolean("isNoScabbard"));
            instance.setSealed(tag.getBoolean("isSealed"));
            instance.setBaseAttackModifier(tag.getFloat("baseAttackModifier"));
            instance.setKillCount(tag.getInt("killCount"));
            instance.setRefine(tag.getInt("RepairCounter"));
            instance.setOwner(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
            instance.setUniqueId(tag.hasUUID("BladeUniqueId") ? tag.getUUID("BladeUniqueId") : UUID.randomUUID());
            instance.setRangeAttackType((RangeAttack)RangeAttack.NONE.valueOf(tag.getString("RangeAttackType")));
            instance.setSlashArtsKey(tag.getString("SpecialAttackType"));
            instance.setDestructable(tag.getBoolean("isDestructable"));
            instance.setDefaultBewitched(tag.getBoolean("isDefaultBewitched"));
            instance.setRarity((Rarity)EnumSetConverter.fromOrdinal(Rarity.values(), tag.getByte("rarityType"), Rarity.COMMON));
            instance.setTranslationKey(tag.getString("translationKey"));
            instance.setCarryType((CarryType)EnumSetConverter.fromOrdinal(CarryType.values(), tag.getByte("StandbyRenderType"), CarryType.DEFAULT));
            instance.setColorCode(tag.getInt("SummonedSwordColor"));
            instance.setEffectColorInverse(tag.getBoolean("SummonedSwordColorInverse"));
            instance.setAdjust(NBTHelper.getVector3d(tag, "adjustXYZ"));
            if (tag.contains("TextureName")) {
                instance.setTexture(new ResourceLocation(tag.getString("TextureName")));
            } else {
                instance.setTexture((ResourceLocation)null);
            }
    
            if (tag.contains("ModelName")) {
                instance.setModel(new ResourceLocation(tag.getString("ModelName")));
            } else {
                instance.setModel((ResourceLocation)null);
            }
    
            instance.setComboRootName(tag.getString("ComboRoot"));
            instance.setComboRootAirName(tag.getString("ComboRootAir"));
        });
    }
}
