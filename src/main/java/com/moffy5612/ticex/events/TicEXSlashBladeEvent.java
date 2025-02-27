package com.moffy5612.ticex.events;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.moffy5612.ticex.TicEX;
import com.moffy5612.ticex.entities.TicEXSlashBladeAbstructSummonedSword;
import com.moffy5612.ticex.handlers.slashblade.TicEXSlashBladeItems;
import com.moffy5612.ticex.items.ToolSlashBlade;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateCapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.ComboState;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.capability.slashblade.RangeAttack;
import mods.flammpfeil.slashblade.capability.slashblade.combo.Extra;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.event.InputCommandEvent;
import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.NBTHelper;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import mods.flammpfeil.slashblade.util.StatHelper;
import mods.flammpfeil.slashblade.util.TargetSelector;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXSlashBladeEvent {
    public static final ResourceLocation ADVANCEMENT_SUMMONEDSWORDS = new ResourceLocation(SlashBlade.modid, "arts/shooting/summonedswords");

    public static void onInputCommand(InputCommandEvent event){
        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getPlayer();
        ItemStack mainHandStack = sender.getMainHandItem();

        boolean onDown = !old.contains(InputCommand.M_DOWN) && current.contains(InputCommand.M_DOWN);

        if(onDown){
            Level worldIn = sender.level;

            sender.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state)->{

                if(sender.experienceLevel <= 0)
                    return;

                sender.giveExperiencePoints(-1);

                AdvancementHelper.grantCriterion(sender, ADVANCEMENT_SUMMONEDSWORDS);

                Optional<Entity> foundTarget = Stream.of(Optional.ofNullable(state.getTargetEntity(sender.level))
                            , RayTraceHelper.rayTrace(sender.level, sender, sender.getEyePosition(1.0f) , sender.getLookAngle(), 12,12, (e)->true)
                                    .filter(r->r.getType() == HitResult.Type.ENTITY)
                                    .filter(r->{
                                        Entity target = ((EntityHitResult) r).getEntity();

                                        boolean isMatch = true;
                                        if(target instanceof LivingEntity livingEntity)
                                            isMatch = TargetSelector.lockon_focus.test(sender, livingEntity);

                                        return isMatch;
                                    }).map(r->((EntityHitResult) r).getEntity()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();

                Vec3 targetPos = foundTarget.map((e)->new Vec3(e.getX(), e.getY() + e.getEyeHeight() * 0.5, e.getZ()))
                        .orElseGet(()->{
                            Vec3 start = sender.getEyePosition(1.0f);
                            Vec3 end = start.add(sender.getLookAngle().scale(40));
                            HitResult result = worldIn.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, sender));
                            return result.getLocation();
                        });

                int counter = StatHelper.increase(sender, SlashBlade.RegistryEvents.SWORD_SUMMONED, 1);
                boolean sided = counter % 2 == 0;
                
                EntityAbstractSummonedSword ss;
                if(mainHandStack.getItem() instanceof IModifiable){
                    ToolStack tool = ToolStack.from(mainHandStack);
                    ss = new TicEXSlashBladeAbstructSummonedSword(SlashBlade.RegistryEvents.SummonedSword, worldIn, tool);
                } else {
                    ss = new EntityAbstractSummonedSword(SlashBlade.RegistryEvents.SummonedSword, worldIn);
                }

                Vec3 pos = sender.getEyePosition(1.0f)
                        .add(VectorHelper.getVectorForRotation( 0.0f, sender.getViewYRot(0) + 90).scale(sided ? 1 : -1));
                ss.setPos(pos.x, pos.y, pos.z);

                Vec3 dir = targetPos.subtract(pos).normalize();
                ss.shoot(dir.x,dir.y,dir.z, 3.0f, 0.0f);


                ss.setOwner(sender);
                ss.setColor(state.getColorCode());
                ss.setRoll(sender.getRandom().nextFloat() * 360.0f);

                worldIn.addFreshEntity(ss);

                sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            });
        }
    }

    public static void onModelBake(final ModelBakeEvent event){
        if(!TicEX.SLASH_BLADE_HANDLER.isModsLoaded())return;
        ModelResourceLocation loc = new ModelResourceLocation(
                ForgeRegistries.ITEMS.getKey(TicEXSlashBladeItems.SLASHBLADE.asItem()), "inventory");
        event.getModelRegistry().put(loc, new BladeModel(event.getModelRegistry().get(loc), event.getModelLoader()));
    }

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

    public static void onItemOverrided(RenderOverrideEvent event){
        if(event.getStack().getItem() instanceof ToolSlashBlade){
            event.setCanceled(true);
        }
    }
}
