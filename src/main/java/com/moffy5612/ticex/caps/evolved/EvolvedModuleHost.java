package com.moffy5612.ticex.caps.evolved;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.IntegerFormatter;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.moffy5612.ticex.handlers.TicEXModuleProvider;
import com.moffy5612.ticex.modifiers.EvolvedModifierTool;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EvolvedModuleHost extends ModuleHostImpl{
    private IToolStackView tool;

    public EvolvedModuleHost(IToolStackView tool) {
        super(
            TechLevel.byIndex(tool.getModifierLevel(TicEXModuleProvider.MODIFIER_EVOLVED.get()) - 1),
            ModuleCfg.staffWidth(TechLevel.byIndex(tool.getModifierLevel(TicEXModuleProvider.MODIFIER_EVOLVED.get()) - 1)),
            ModuleCfg.staffHeight(TechLevel.byIndex(tool.getModifierLevel(TicEXModuleProvider.MODIFIER_EVOLVED.get()) - 1)),
            "tconTools",
            false,
            new ModuleCategory[]{
                ModuleCategory.ENERGY,
                ModuleCategory.MELEE_WEAPON,
                ModuleCategory.MINING_TOOL
            }
        );

        this.addPropertyBuilder(props -> {
            AOEData aoe = this.getModuleData(ModuleTypes.AOE);
            if(aoe != null){
                DecimalProperty attackAoEProperty = new DecimalProperty("attack_aoe", aoe.aoe() * 1.5).range(0, aoe.aoe() * 1.5).setFormatter(DecimalFormatter.AOE_1);
                IntegerProperty miningAoEProperty = new IntegerProperty("mining_aoe", aoe.aoe()).range(0, aoe.aoe()).setFormatter(IntegerFormatter.AOE);
                BooleanProperty aoeSafeProperty = new BooleanProperty("aoe_safe", false).setFormatter(BooleanFormatter.ENABLED_DISABLED);
                props.add(attackAoEProperty);
                props.add(miningAoEProperty);
                props.add(aoeSafeProperty);
            }
        });

        this.tool = tool;
    }

    @Override
    public void addModule(ModuleEntity<?> entity, ModuleContext context) {
        super.addModule(entity, context);
        writeToPersistentData();
    }

    @Override
    public void removeModule(ModuleEntity<?> entity, ModuleContext context) {
        super.removeModule(entity, context);
        writeToPersistentData();
    }

    @Override
    public TechLevel getHostTechLevel() {
        readFromPersistentData();
        return super.getHostTechLevel();
    }

    private void writeToPersistentData(){
        tool.getPersistentData().put(EvolvedModifierTool.MODULE_HOST_LOCATION, serializeNBT());
    }

    private void readFromPersistentData(){
        deserializeNBT(tool.getPersistentData().getCompound(EvolvedModifierTool.MODULE_HOST_LOCATION));
        getProperties().forEach(property->{
            if(property instanceof BooleanProperty){
                ((BooleanProperty)property).setChangeListener(this::writeToPersistentData);
            } else if (property instanceof DecimalProperty){
                ((DecimalProperty)property).setChangeListener(this::writeToPersistentData);
            } else if(property instanceof IntegerProperty) {
                ((IntegerProperty)property).setChangeListener(this::writeToPersistentData);
            }
        });
    }

}
