package com.moffy5612.ticex.modifiers;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.damage.IDraconicDamage;
import com.brandon3055.draconicevolution.api.modules.ModuleHelper;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.moffy5612.ticex.TicEXConfig;
import com.moffy5612.ticex.TicEXReference;
import com.moffy5612.ticex.integration.materialis.MaterialisModifierUtils;
import com.moffy5612.ticex.utils.TicEXDEUtils;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.math.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EvolvedModifierTool extends Modifier{

    Random rand = new Random();

    public static final ResourceLocation MODULE_HOST_LOCATION = new ResourceLocation(TicEXReference.MOD_ID, "module_host");
    public static final ResourceLocation OP_STORAGE_LOCATION = new ResourceLocation(TicEXReference.MOD_ID, "op_storage");
    
    public static final String STORED_OP_KEY = "tooltip.ticex.stored_op";

    @Override
    public int getPriority() {
        return 999;
    }

    @Override
    public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
        return 0;
    }

    @Override
    public void addInformation(IToolStackView tool, int level, Player player, List<Component> tooltip,
    		TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    	super.addInformation(tool, level, player, tooltip, tooltipKey, tooltipFlag);
    	
    	ItemStack stack = getTool(player);
    	
    	if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("[Modular Item]").withStyle(ChatFormatting.BLUE));
        }
    	
    	ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElse(null);
        if (host != null) {
            host.getModuleEntities().forEach(e -> e.addHostHoverText(stack, player.getLevel(), tooltip, tooltipFlag));
            host.getInstalledTypes().map(host::getModuleData).filter(Objects::nonNull).forEach(data -> data.addHostHoverText(stack, player.getLevel(), tooltip, tooltipFlag));
        }
    	
        EnergyUtils.addEnergyInfo(stack, tooltip);
    }

    @Override
    public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage,
            float baseKnockback, float knockback) {
        Player player = context.getPlayerAttacker();
        if(player != null){
            Entity target = context.getTarget();
            if(target != null) {
                ItemStack stack = player.getItemInHand(context.getHand());
                ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);
                int attackDamage = getDamageBonus(host, opStorage);
                int extracted = opStorage.extractEnergy(tool.getStats().getInt(ToolStats.ATTACK_DAMAGE) + attackDamage, false);
                hurt(player, target, stack, Math.min(extracted, attackDamage));
                double aoe = getAttackAoe(host);
                if (aoe > 0 && target instanceof LivingEntity) {
                    dealAOEDamage(player, (LivingEntity)target, stack, Math.min(extracted, attackDamage) * 0.8F, aoe);
                }
            } 
        }
        return 0f;
    }

    

    @SuppressWarnings("null")
    @Override
    public void afterBlockBreak(IToolStackView tool, int level, ToolHarvestContext context) {
        Player player = context.getPlayer();
        if(player != null && !context.isAOE()){
            ItemStack stack = getTool(player);
            ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            IOPStorage storage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

            int aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe();
            boolean aoeSafe = true;
            if (host instanceof PropertyProvider) {
                if (((PropertyProvider) host).hasInt("mining_aoe")) {
                    aoe = ((PropertyProvider) host).getInt("mining_aoe").getValue();
                }
                if (((PropertyProvider) host).hasBool("aoe_safe")) {
                    aoeSafe = ((PropertyProvider) host).getBool("aoe_safe").getValue();
                }
            }
            
            breakAOEBlocks(host, storage, stack, context.getPos(), context.getSideHit(), aoe + tool.getModifierLevel(TinkerModifiers.expanded.get()), 0, player, aoeSafe, context);
        }
    }

    private void dealAOEDamage(Player player, LivingEntity target, ItemStack stack, float damage, double aoe) {
        IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

        List<LivingEntity> entities = player.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(aoe, 0.25D, aoe));
        double aoeAngle = 100;
        double yaw = player.getYRot() - 180;
        int fireAspect = EnchantmentHelper.getFireAspect(player);

        for (LivingEntity entity : entities) {
            float distance = player.distanceTo(entity);
            if (entity == player || entity == target || player.isAlliedTo(entity) || distance < 1 || entity.distanceTo(target) > aoe) continue;
            double angle = Math.atan2(player.getX() - entity.getX(), player.getZ() - entity.getZ()) * MathHelper.todeg;
            double relativeAngle = Math.abs((angle + yaw) % 360);
            if (relativeAngle <= aoeAngle / 2 || relativeAngle > 360 - (aoeAngle / 2)) {
                boolean lit = false;
                float health = entity.getHealth();
                if (fireAspect > 0 && !entity.isOnFire()) {
                    lit = true;
                    entity.setSecondsOnFire(1);
                }

                int extracted = opStorage.extractEnergy((int)damage, false);
                if (hurt(player, entity, stack, extracted)) {
                    float damageDealt = health - entity.getHealth();
                    entity.knockback(0.4F, MathHelper.sin(player.getYRot() * MathHelper.torad), (-MathHelper.cos(player.getYRot() * MathHelper.torad)));

                    if (fireAspect > 0) {
                        entity.setSecondsOnFire(fireAspect * 4);
                    }

                    if (player.level instanceof ServerLevel && damageDealt > 2.0F) {
                        int k = (int)((double)damage * 0.5D);
                        ((ServerLevel)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5D), entity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }

                    player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
                    if (player.level instanceof ServerLevel && damageDealt > 2.0F) {
                        int k = (int) ((double) damageDealt * 0.5D);
                        ((ServerLevel) player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5D), entity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }
                } else if (lit) {
                    entity.clearFire();
                }
            }
        }
    }

    private int getDamageBonus(ModuleHost host, IOPStorage opStorage){
        double damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0)).damagePoints();
        if (opStorage.getEnergyStored() < EquipCfg.energyAttack * damage) {
            damage = 0;
        }
        return (int)Math.round(damage + ((TicEXDEUtils.getTier(host.getHostTechLevel()).getAttackDamageBonus() * EquipCfg.staffDamageMultiplier) - 1));
    }

    private double getAttackAoe(ModuleHost host){
        double aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe() * 1.5;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("attack_aoe")) {
            aoe = ((PropertyProvider) host).getDecimal("attack_aoe").getValue();
        }
        return aoe;
    }

    private boolean hurt(Player player, Entity target, ItemStack stack, int damage){
        boolean result = false;
        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

        if(ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaKill(stack)){
            result = target.hurt(new PlayerDraconicDamage(player, host.getHostTechLevel()), Integer.MAX_VALUE);
            if(!(target instanceof Player)){
                if(target instanceof LivingEntity && target.isAlive()){
                    LivingEntity living = (LivingEntity)target;
                    living.setLastHurtByMob(player);
                    living.setLastHurtByPlayer(player);
                    player.level.broadcastEntityEvent(living, (byte)29);
                    living.setHealth(living.getHealth() - (TicEXConfig.INSTANT_KILL_ALL.get() ? Integer.MAX_VALUE : damage));
                }
            }
        } else {
            result = target.hurt(new PlayerDraconicDamage(player, host.getHostTechLevel()), damage);
        }

        if(!result){
            opStorage.receiveEnergy(damage, false);
        }

        return result;
    }

    private boolean breakAOEBlocks(ModuleHost host, IOPStorage storage, ItemStack stack, BlockPos pos, Direction sideHit, int breakRadius, int breakDepth, Player player, boolean aoeSafe, ToolHarvestContext context) {
        BlockState blockState = player.level.getBlockState(pos);

        InventoryDynamic inventoryDynamic = new InventoryDynamic();
        float refStrength = blockStrength(blockState, player, player.level, pos);
        Pair<BlockPos, BlockPos> aoe = getMiningArea(pos, sideHit, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = BlockPos.betweenClosedStream(aoe.key(), aoe.value()).map(BlockPos::new).toList();

        if (aoeSafe) {
            for (BlockPos block : aoeBlocks) {
                if (!player.level.isEmptyBlock(block) && player.level.getBlockEntity(block) != null) {
                    if (player.level.isClientSide) player.sendMessage(new TranslatableComponent("item_prop.draconicevolution.aoe_safe.blocked"), Util.NIL_UUID);
                    else ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(((ServerPlayer) player).level, block));
                    return true;
                }
            }
        }

        aoeBlocks.forEach(block -> breakAOEBlock(stack, player.level, block, player, refStrength, inventoryDynamic, rand.nextInt(Math.max(5, (breakRadius * breakDepth) / 5)) == 0, context));
        List<ItemEntity> items = player.level.getEntitiesOfClass(ItemEntity.class, new AABB(aoe.key(), aoe.value().offset(1, 1, 1)));
        for (ItemEntity item : items) {
            if (!player.level.isClientSide && item.isAlive()) {
                InventoryUtils.insertItem(inventoryDynamic, item.getItem(), false);
                item.discard();
            }
        }

        ModuleHelper.handleItemCollection(player, host, storage, inventoryDynamic);
        return true;
    }

    private boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if(ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaMine(stack)) return true;
        return stack.isCorrectToolForDrops(state);
    }

    private  Pair<BlockPos, BlockPos> getMiningArea(BlockPos pos, Direction direction, Player player, int breakRadius, int breakDepth) {

        int sideHit = direction.get3DDataValue();

        int xMax = breakRadius;
        int xMin = breakRadius;
        int yMax = breakRadius;
        int yMin = breakRadius;
        int zMax = breakRadius;
        int zMin = breakRadius;
        int yOffset = 0;

        switch (sideHit) {
            case 0 -> {
                yMax = breakDepth;
                yMin = 0;
                zMax = breakRadius;
            }
            case 1 -> {
                yMin = breakDepth;
                yMax = 0;
                zMax = breakRadius;
            }
            case 2 -> {
                xMax = breakRadius;
                zMin = 0;
                zMax = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 3 -> {
                xMax = breakRadius;
                zMax = 0;
                zMin = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 4 -> {
                xMax = breakDepth;
                xMin = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
            case 5 -> {
                xMin = breakDepth;
                xMax = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
        }

        if (breakRadius == 0) {
            yOffset = 0;
        }

        return new Pair<>(pos.offset(-xMin, yOffset - yMin, -zMin), pos.offset(xMax, yOffset + yMax, zMax));
    }


    static float blockStrength(BlockState state, Player player, Level world, BlockPos pos) {
        float hardness = state.getDestroySpeed(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!ForgeHooks.isCorrectToolForDrops(state, player)) {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    private void breakAOEBlock(ItemStack stack, Level world, BlockPos pos, Player player, float refStrength, InventoryDynamic inventory, boolean breakFX, ToolHarvestContext context) {
        if (world.isEmptyBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        if (!isCorrectToolForDrops(stack, state)) {
            return;
        }

        ToolHarvestContext newContext = context.forPosition(pos, state);
        if(stack.getItem() instanceof SwordItem && ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaMine(stack)){
            state.getBlock().playerWillDestroy(world, pos, state, player);
            TicEXDEUtils.breakExtraBlock(ToolStack.from(stack), stack, newContext, inventory);
        } else {
            ToolHarvestLogic.breakExtraBlock(ToolStack.from(stack), stack, newContext);
        }
    }

    private ItemStack getTool(LivingEntity entity){
        ItemStack result = entity.getMainHandItem();
        if(ItemStack.isSame(result, ItemStack.EMPTY)){
            result = entity.getOffhandItem();
        }
        return result;
    }

    private class PlayerDraconicDamage extends EntityDamageSource implements IDraconicDamage{

        private TechLevel techLevel;

        public PlayerDraconicDamage(Entity entity, TechLevel techLevel) {
            super("player", entity);
            this.techLevel = techLevel;
        }

        @Override
        public boolean isMagic() {
            return true;
        }

        @Override
        public TechLevel getTechLevel(ItemStack stack) {
            return this.techLevel;
        }
    }
}
