package com.moffy5612.ticex.items;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.moffy5612.ticex.TicEXReference;
import com.moffy5612.ticex.client.renderer.TicEXSlashBladeISTER;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.integration.materialis.MaterialisModifierUtils;
import com.moffy5612.ticex.modifiers.KoshiraeModifier;
import com.moffy5612.ticex.utils.TicEXUtils;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifiableItemUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerToolActions;
public class ToolSlashBlade extends ItemSlashBlade implements IModifiableDisplay{

    public static final UUID KOSHIRAE_BONUS = UUID.fromString("2fa5b0ad-5980-4550-8878-a3c4fcef3179");
    private final ToolDefinition toolDefinition;

    public ToolSlashBlade(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder, ToolDefinition definition) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.toolDefinition = definition;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    @Override
    public Item asItem() {
        return this;
    }

    @Override
    public ItemStack getRenderTool() {
        return ItemStack.EMPTY;
    }
    
    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    @SuppressWarnings("null")
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @SuppressWarnings("null")
    @Override
    public void verifyTagAfterLoad(CompoundTag nbt) {
        ToolStack.verifyTag(this, nbt, getToolDefinition());
    }

    @SuppressWarnings("null")
    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        ToolStack.ensureInitialized(stack, getToolDefinition());
    }

    @SuppressWarnings("null")
    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || ModifierUtil.checkVolatileFlag(stack, SHINY);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (!canBeDepleted()) {
        return 0;
        }
        ToolStack tool = ToolStack.from(stack);
        int durability = tool.getStats().getInt(ToolStats.DURABILITY);
        return tool.isBroken() ? durability + 1 : durability;
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (!canBeDepleted()) {
        return 0;
        }
        return super.getDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if(canBeDepleted()){
            super.setDamage(stack, damage);
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity,
            java.util.function.Consumer<T> onBroken) {
        ToolDamageUtil.handleDamageItem(stack, super.damageItem(stack, amount, entity, onBroken), entity, onBroken);
        stack.getCapability(BLADESTATE).ifPresent((state)->{
            state.setBroken(ToolDamageUtil.isBroken(stack));
        });
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) || ToolDamageUtil.showDurabilityBar(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ToolDamageUtil.getRGBDurabilityForDisplay(stack);
    }

    public int getBarWidth(ItemStack pStack) {
        return ToolDamageUtil.getDamageForDisplay(pStack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        Multimap<Attribute, AttributeModifier> extraMap = (Multimap<Attribute, AttributeModifier>)(nbt != null && slot.getType() == Type.HAND ? this.getAttributeModifiers((IToolStackView)ToolStack.from(stack), (EquipmentSlot)slot) : ArrayListMultimap.create());
        
        if (slot == EquipmentSlot.MAINHAND) {
            LazyOptional<ISlashBladeState> state = stack.getCapability(BLADESTATE);
            state.ifPresent(s -> {
                float baseAttackModifier = s.getBaseAttackModifier();
                AttributeModifier base = new AttributeModifier(KOSHIRAE_BONUS,
                        "Weapon modifier",
                        (double) (baseAttackModifier * 0.7),
                        AttributeModifier.Operation.ADDITION);
                extraMap.put(Attributes.ATTACK_DAMAGE,base);

                float rankAttackAmplifier = s.getAttackAmplifier();
                extraMap.put(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(ATTACK_DAMAGE_AMPLIFIER,
                                "Weapon amplifier",
                                (double)(rankAttackAmplifier),
                                AttributeModifier.Operation.ADDITION));

                extraMap.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(PLAYER_REACH_AMPLIFIER,
                        "Reach amplifer",
                        s.isBroken() ? 0 : 1.5, AttributeModifier.Operation.ADDITION));

            });
        }
        return extraMap;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> attribute = ModifiableItemUtil.getMeleeAttributeModifiers(tool, slot);
        Multimap<Attribute, AttributeModifier> mutableAttribute = ArrayListMultimap.create(attribute);
        
        return mutableAttribute;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return !ToolDamageUtil.isBroken(stack) && this.toolDefinition.getData().canPerformAction(TinkerToolActions.SHIELD_DISABLE);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if(ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaMine(stack))return true;
        return ToolHarvestLogic.isEffective(ToolStack.from(stack), state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
            LivingEntity entityLiving) {
        CompoundTag nbt = stack.getTag();
        if(nbt != null && ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaMine(stack)){
            ToolStack tool = ToolStack.from(stack);
            if (!tool.isBroken() && entityLiving instanceof Player) {
                Player player = (Player)entityLiving;
                ToolHarvestContext context = new ToolHarvestContext((ServerLevel)worldIn, entityLiving, state, pos, BlockSideHitListener.getSideHit(player), true, true);

                state.getBlock().playerWillDestroy(worldIn, pos, state, player);

                if (worldIn instanceof ServerLevel) {
                    ItemStack toolStack = player.getItemBySlot(EquipmentSlot.MAINHAND);

                    TicEXUtils.breakBlock(tool, toolStack, context);
                }
            }
        }
        return ToolHarvestLogic.mineBlock(stack, worldIn, state, pos, entityLiving);
    }

    @SuppressWarnings("null")
    @Override
    public float getDestroySpeed(ItemStack p_43288_, BlockState p_43289_) {
        if(ModList.get().isLoaded("materialis") && MaterialisModifierUtils.hasInstaMine(p_43288_)){
            return Float.MAX_VALUE;
        }
        return ToolHarvestLogic.getDestroySpeed(p_43288_, p_43289_);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        ModifiableItemUtil.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        ToolStack tool = ToolStack.from(stack);
        stack.getCapability(BLADESTATE).ifPresent(state->{
            if(state.getModel().isPresent() && tool.getModifierLevel(TicEXModuleProvider.MODIFIER_KOSHIRAE.get()) < 1){
                KoshiraeModifier.deserializeNBT(stack.getCapability(BLADESTATE), new CompoundTag());
            } else if (state.getModel().isEmpty() && tool.getModifierLevel(TicEXModuleProvider.MODIFIER_KOSHIRAE.get()) > 0){
                tool.removeModifier(TicEXModuleProvider.MODIFIER_KOSHIRAE.getId(), 1);
            }
        });
    }

    @Override
    @Nullable
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        BladeItemEntity e = new BladeItemEntity(SlashBlade.RegistryEvents.BladeItem, world);
        e.restoreFrom(location);
        e.init();
        return e;
    }
    
    @SuppressWarnings("deprecation")
    protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
      IModDataView volatileData = toolStack.getVolatileData();
      if (volatileData.getBoolean(NO_INTERACTION)) {
         return false;
      } else {
         boolean deferOffhand = volatileData.getBoolean(DEFER_OFFHAND);
         if (hand == InteractionHand.OFF_HAND) {
            return deferOffhand || !toolStack.hasTag(Items.TWO_HANDED);
         } else {
            return player == null || !deferOffhand || player.getOffhandItem().isEmpty();
         }
      }
   }

   @Override
   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
       ToolStack tool = ToolStack.from(stack);
      InteractionHand hand = context.getHand();
      if (shouldInteract(context.getPlayer(), tool, hand)) {
         Iterator<ModifierEntry> var5 = tool.getModifierList().iterator();

         while(var5.hasNext()) {
            ModifierEntry entry = (ModifierEntry)var5.next();
            InteractionResult result = ((BlockInteractionModifierHook)entry.getHook(TinkerHooks.BLOCK_INTERACT)).beforeBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
            if (result.consumesAction()) {
               return result;
            }
         }
      }

      return InteractionResult.PASS;
   }

   @SuppressWarnings("null")
   @Override
   public InteractionResult useOn(UseOnContext context) {
    ToolStack tool = ToolStack.from(context.getItemInHand());
    InteractionHand hand = context.getHand();
    if (shouldInteract(context.getPlayer(), tool, hand)) {
       Iterator<ModifierEntry> var4 = tool.getModifierList().iterator();

       while(var4.hasNext()) {
          ModifierEntry entry = (ModifierEntry)var4.next();
          InteractionResult result = ((BlockInteractionModifierHook)entry.getHook(TinkerHooks.BLOCK_INTERACT)).afterBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
          if (result.consumesAction()) {
             return result;
          }
       }
    }

    return InteractionResult.PASS;
   }

   @SuppressWarnings("null")
   @Override
   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target,
           InteractionHand hand) {
       ToolStack tool = ToolStack.from(stack);
      if (shouldInteract(playerIn, tool, hand)) {
         Iterator<ModifierEntry> var6 = tool.getModifierList().iterator();

         while(var6.hasNext()) {
            ModifierEntry entry = (ModifierEntry)var6.next();
            InteractionResult result = ((EntityInteractionModifierHook)entry.getHook(TinkerHooks.ENTITY_INTERACT)).afterEntityUse(tool, entry, playerIn, target, hand, InteractionSource.RIGHT_CLICK);
            if (result.consumesAction()) {
               return result;
            }
         }
      }

      return InteractionResult.PASS;
   }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
       ItemStack stack = playerIn.getItemInHand(hand);
        ToolStack tool = ToolStack.from(stack);
        if (shouldInteract(playerIn, tool, hand)) {
            Iterator<ModifierEntry> var6 = tool.getModifierList().iterator();

            while(var6.hasNext()) {
                ModifierEntry entry = (ModifierEntry)var6.next();
                InteractionResult result = ((GeneralInteractionModifierHook)entry.getHook(TinkerHooks.CHARGEABLE_INTERACT)).onToolUse(tool, entry, playerIn, hand, InteractionSource.RIGHT_CLICK);
                if (result.consumesAction()) {
                return new InteractionResultHolder<ItemStack>(result, stack);
                }
            }

            if (hand == InteractionHand.MAIN_HAND && stack.is(Items.TWO_HANDED) && !tool.getVolatileData().getBoolean(DEFER_OFFHAND)) {
                return InteractionResultHolder.consume(stack);
            }
        }

        ToolInventoryCapability.tryOpenContainer(stack, tool, playerIn, Util.getSlotType(hand));
        
        InteractionResultHolder<ItemStack> result = super.use(worldIn, playerIn, hand);

        return result;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = ModifierUtil.getActiveModifier(tool);
        if (activeModifier != null) {
            ((GeneralInteractionModifierHook)activeModifier.getHook(TinkerHooks.CHARGEABLE_INTERACT)).onUsingTick(tool, activeModifier, player, count);
        }
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        if (super.canContinueUsing(oldStack, newStack) && oldStack != newStack) {
           ModifierUtil.finishUsingItem(ToolStack.from(oldStack));
        }
  
        return super.canContinueUsing(oldStack, newStack);
    }

    @SuppressWarnings({ "null", "deprecation" })
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = ModifierUtil.getActiveModifier(tool);
        ModifierUtil.finishUsingItem(tool);
        if (activeModifier != null) {
           ((GeneralInteractionModifierHook)activeModifier.getHook(TinkerHooks.CHARGEABLE_INTERACT)).onFinishUsing(tool, activeModifier, entityLiving);
           return stack;
        } else {
           Iterator<ModifierEntry> var6 = tool.getModifierList().iterator();
  
           ModifierEntry entry;
           do {
              if (!var6.hasNext()) {
                 return stack;
              }
  
              entry = (ModifierEntry)var6.next();
           } while(!((GeneralInteractionModifierHook)entry.getHook(TinkerHooks.GENERAL_INTERACT)).onFinishUsing(tool, entry, entityLiving));
  
           return stack;
        }
     }

    @SuppressWarnings("deprecation")
    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = ModifierUtil.getActiveModifier(tool);
        ModifierUtil.finishUsingItem(tool);
        if (activeModifier != null) {
           ((GeneralInteractionModifierHook)activeModifier.getHook(TinkerHooks.CHARGEABLE_INTERACT)).onStoppedUsing(tool, activeModifier, entityLiving, timeLeft);
        } else {
           Iterator<ModifierEntry> var7 = tool.getModifierList().iterator();
  
           boolean result;
           do {
              if (!var7.hasNext()) {
                 return;
              }
  
              ModifierEntry entry = (ModifierEntry)var7.next();
              result = ((GeneralInteractionModifierHook)entry.getHook(TinkerHooks.GENERAL_INTERACT)).onStoppedUsing(tool, entry, entityLiving, timeLeft);
           } while(!result);
  
        }
     }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return super.canPerformAction(stack, toolAction);
    }

    @SuppressWarnings("null")
    @Override
    public int getDefaultTooltipHideFlags(ItemStack stack) {
      return TooltipUtil.getModifierHideFlags(this.getToolDefinition());
   }

   @Override
   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         ToolBuildHandler.addDefaultSubItems(this, items, new MaterialVariantId[0]);
      }

   }
    @SuppressWarnings("null")
    @Override
     public Component getName(ItemStack stack) {
        return TooltipUtil.getDisplayName(stack, this.getToolDefinition());
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
      return this.shouldCauseReequipAnimation(oldStack, newStack, false);
   }

   @Override
   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return ModifiableItemUtil.shouldCauseReequip(oldStack, newStack, slotChanged);
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        TooltipUtil.addInformation(this, stack, worldIn, tooltip, SafeClientAccess.getTooltipKey(), flagIn);
   }

   @Override
   public boolean hurtEnemy(ItemStack stackF, LivingEntity target, LivingEntity attacker) {
        if(target == null)return false;
        if(!(attacker instanceof Player))return super.hurtEnemy(stackF, target, attacker);

        ToolStack tool = ToolStack.from(stackF);
        List<ModifierEntry> modifiers = tool.getModifierList();
        boolean isCritical = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.onClimbable() && !attacker.isInWater() && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger() && (LivingEntity)target != null && !attacker.isSprinting();
        ToolAttackContext context = new ToolAttackContext(attacker, (Player)attacker, InteractionHand.MAIN_HAND, Util.getSlotType(InteractionHand.MAIN_HAND), (Entity)target, target, isCritical, 0, false);
        float damage = ToolAttackUtil.getAttributeAttackDamage(tool, attacker, Util.getSlotType(InteractionHand.MAIN_HAND));

        ModifierEntry entry;
        
        for(Iterator<ModifierEntry> var16 = modifiers.iterator(); var16.hasNext(); damage = entry.getModifier().getEntityDamage(tool, entry.getLevel(), context, damage, damage)) {
            entry = (ModifierEntry)var16.next();
        }
        
        for(Iterator<ModifierEntry> var17 = modifiers.iterator(); var17.hasNext(); damage = entry.getModifier().beforeEntityHit(tool, entry.getLevel(), context, damage, 0, 0)) {
            entry = (ModifierEntry)var17.next();
        }

        boolean result = super.hurtEnemy(stackF, target, attacker);

        if(result){
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

        return result;
   }

   @Override
   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new TicEXSlashBladeISTER(getDefaultModelsLocation());
            }
        });
   }

   public ResourceLocation getDefaultModelsLocation(){
        return new ResourceLocation(TicEXReference.MOD_ID, "slashblade");
   }
}
