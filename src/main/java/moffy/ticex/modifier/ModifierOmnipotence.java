package moffy.ticex.modifier;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import committee.nova.mods.avaritia.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierOmnipotence extends NoLevelsModifier implements ProjectileHitModifierHook, MeleeHitModifierHook, BreakSpeedModifierHook{
    @Override
    public int getPriority() {
        return 999;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.BREAK_SPEED);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage,
            float baseKnockback, float knockback) {
        LivingEntity victim = context.getLivingTarget();
        if (victim instanceof EnderDragon) {
            victim.setInvulnerable(false);
        } else if (victim instanceof Player pvp) {
            if (ToolUtils.isInfinite(pvp)) {
                pvp.level().explode(context.getPlayerAttacker(), pvp.getBlockX(), pvp.getBlockY(), pvp.getBlockZ(), 25.0F, Level.ExplosionInteraction.MOB);
                return 0;
            } else {
                victim.setInvulnerable(false);
            }

        } else {
            victim.setInvulnerable(false);

        }
        return MeleeHitModifierHook.super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context,
            float damageDealt){
        LivingEntity attackerEntity = context.getAttacker();
        LivingEntity victim = context.getLivingTarget();
        if(damageDealt > 0 && victim.isAlive() && context.getLevel() instanceof ServerLevel){
            dealInfinityDamage(context.getLevel(), attackerEntity, victim);
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, EntityHitResult hit, LivingEntity attacker, LivingEntity target) {
        boolean result = ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit,
        attacker, target);

        if(result){
            dealInfinityDamage(attacker.level(), attacker, target);
        }

        return result;
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry entry, BreakSpeed event, Direction direction, boolean isEffective,
            float miningSpeedModifier) {
        BlockState state = event.getState();
        BlockPos pos = event.getPosition().get();
        Player player = event.getEntity();
        Level level = player.level();
        if(!state.canHarvestBlock(player.level(), event.getPosition().get(), player)){
            state.getBlock().playerDestroy(level, player, pos, state, level.getBlockEntity(pos), player.getMainHandItem());
        }
        event.setNewSpeed(Float.MAX_VALUE);
    }

    private void dealInfinityDamage(Level level, LivingEntity attackerEntity, LivingEntity victim){
        victim.setHealth(0);
        victim.die(new DamageSource(attackerEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.INFINITY), attackerEntity, victim));
        if(victim.isAlive()){
            ServerLevel serverLevel = (ServerLevel)level;
            for(ItemEntity drop : victim.captureDrops()){
                level.addFreshEntity(drop);
            }
            victim.kill();
            attackerEntity.killedEntity(serverLevel, victim);
            ((Player)attackerEntity).killedEntity(null, attackerEntity);
            serverLevel.broadcastEntityEvent(victim, (byte)3);
        }
    }
}
