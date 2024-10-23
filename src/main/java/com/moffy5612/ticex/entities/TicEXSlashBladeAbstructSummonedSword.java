package com.moffy5612.ticex.entities;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.moffy5612.ticex.utils.TicEXUtils;

import mods.flammpfeil.slashblade.SlashBlade.RegistryEvents;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

public class TicEXSlashBladeAbstructSummonedSword extends EntityAbstractSummonedSword{

    @Nullable
    private ToolStack tool;

    public TicEXSlashBladeAbstructSummonedSword(EntityType<? extends Projectile> entityTypeIn, Level worldIn){
        this(entityTypeIn, worldIn, ItemStack.EMPTY);
    }

    public TicEXSlashBladeAbstructSummonedSword(EntityType<? extends Projectile> entityTypeIn, Level worldIn, ItemStack stack) {
        super(entityTypeIn, worldIn);
        if(stack.getItem() instanceof IModifiable)tool = ToolStack.from(stack);
        else tool = null;
    }

    public TicEXSlashBladeAbstructSummonedSword(EntityType<? extends Projectile> entityTypeIn, Level worldIn, ToolStack tool) {
        super(entityTypeIn, worldIn);
        this.tool = tool;
    }

    public ToolStack getToolStack(){
        return this.tool;
    }

    public static TicEXSlashBladeAbstructSummonedSword createInstance(PlayMessages.SpawnEntity packet, Level worldIn){
        return new TicEXSlashBladeAbstructSummonedSword(RegistryEvents.SummonedSword, worldIn);
    }

    @SuppressWarnings("null")
    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        Entity shooter = this.getShooter();
        
        if(target == null){
            Entity closest = TicEXUtils.getEntityClosestTo(this);
            if(closest != null && !closest.equals(shooter) && this.distanceTo(closest) < 1.5f){
                target = closest;
            }
        }

        if(target == null)return;
        if(shooter instanceof Player){
            Player attacker = (Player)shooter;
            ItemStack stackF = attacker.getMainHandItem();
            ToolStack tool = ToolStack.from(stackF);
            List<ModifierEntry> modifiers = tool.getModifierList();
            boolean isCritical = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.onClimbable() && !attacker.isInWater() && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger() && (LivingEntity)target != null && !attacker.isSprinting();
            ToolAttackContext context = new ToolAttackContext((LivingEntity)attacker, (Player)attacker, InteractionHand.MAIN_HAND, Util.getSlotType(InteractionHand.MAIN_HAND), target, null, isCritical, 0, false);
            float damage = ToolAttackUtil.getAttributeAttackDamage(tool, attacker, Util.getSlotType(InteractionHand.MAIN_HAND));

            ModifierEntry entry;
            
            for(Iterator<ModifierEntry> var16 = modifiers.iterator(); var16.hasNext(); damage = entry.getModifier().getEntityDamage(tool, entry.getLevel(), context, damage, damage)) {
                entry = (ModifierEntry)var16.next();
            }
            
            for(Iterator<ModifierEntry> var17 = modifiers.iterator(); var17.hasNext(); damage = entry.getModifier().beforeEntityHit(tool, entry.getLevel(), context, damage, 0, 0)) {
                entry = (ModifierEntry)var17.next();
            }
            
            super.onHitEntity(hitResult);

            if(this.canHitEntity(target)){
                for(Iterator<ModifierEntry> var41 = modifiers.iterator(); var41.hasNext(); entry.getModifier().afterEntityHit(tool, entry.getLevel(), context, damage)) {
                    entry = (ModifierEntry)var41.next();
                }
            }else{
                Iterator<ModifierEntry> var37 = modifiers.iterator();
    
                while(var37.hasNext()) {
                    entry = (ModifierEntry)var37.next();
                    entry.getModifier().failedEntityHit(tool, entry.getLevel(), context);
                }
            }
            
        } else {
            super.onHitEntity(hitResult);
            return;
        }
    }
}
