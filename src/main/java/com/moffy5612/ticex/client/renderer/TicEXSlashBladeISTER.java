package com.moffy5612.ticex.client.renderer;

import java.util.EnumSet;
import java.util.Optional;

import java.awt.Color;

import com.google.gson.JsonObject;
import com.moffy5612.ticex.items.ToolSlashBlade;
import com.moffy5612.ticex.utils.TicEXSlashBladeAssetsLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateCapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXSlashBladeISTER extends BlockEntityWithoutLevelRenderer{

    public ResourceLocation modelLocation;

    public TicEXSlashBladeISTER(ResourceLocation modelLocation) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.modelLocation = modelLocation;
    }

    @SuppressWarnings("null")
    @Override
    public void renderByItem(ItemStack stack, TransformType transformType, PoseStack matrixStack,
            MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if(!(stack.getItem() instanceof ToolSlashBlade)) return;

        if(stack.hasTag() && stack.getTag().contains(ItemSlashBlade.ICON_TAG_KEY)){
            stack.readShareTag(stack.getTag());
            stack.removeTagKey(ItemSlashBlade.ICON_TAG_KEY);
        }

        renderBlade(stack, transformType, matrixStack, bufferSource, combinedLightIn, combinedOverlayIn);
    }

    public boolean renderBlade(ItemStack stack, ItemTransforms.TransformType transformType , PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn){

        if(transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemTransforms.TransformType.NONE) {

            if(BladeModel.user == null)
                return false;

            EnumSet<SwordType> types = SwordType.from( stack);

            boolean handle = false;

            if(!types.contains(SwordType.NoScabbard)) {
                handle = BladeModel.user.getMainArm() == HumanoidArm.RIGHT ?
                        transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                        transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            }

            if(handle){
                TicEXBladeFirstPersonRenderer.INSTANCE.render(matrixStack, bufferIn, combinedLightIn);
            }

            return false;
        }



        try(MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)) {

            matrixStack.translate(0.5f, 0.5f, 0.5f);

            if (transformType == ItemTransforms.TransformType.GROUND) {
                matrixStack.translate(0, 0.15f, 0);
                renderIcon(stack, matrixStack, bufferIn, combinedLightIn,0.005f);
            } else if (transformType == ItemTransforms.TransformType.GUI) {
                renderIcon(stack, matrixStack, bufferIn, combinedLightIn,0.008f, true);
            } else if (transformType == ItemTransforms.TransformType.FIXED) {
                if (stack.isFramed() && stack.getFrame() instanceof BladeStandEntity) {
                    renderModel(stack, matrixStack, bufferIn, combinedLightIn);
                } else {
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
                    renderIcon(stack, matrixStack, bufferIn, combinedLightIn,0.0095f);
                }
            }else{
                renderIcon(stack, matrixStack, bufferIn, combinedLightIn,0.0095f);
            }
        }

        return true;
    }

    private void renderIcon(ItemStack stack, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, float scale){
        renderIcon(stack, matrixStack, bufferIn, lightIn, scale, false);
    }
    private void renderIcon(ItemStack stack, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, float scale, boolean renderDurability){

        ToolStack tool = ToolStack.from(stack);
        java.util.List<MaterialVariant> materials = tool.getMaterials().getList();

        matrixStack.scale(scale, scale, scale);

        EnumSet<SwordType> types = SwordType.from(stack);

        Optional<JsonObject>objectOptional = TicEXSlashBladeAssetsLoader.loadSlashBladeAssets(modelLocation);
        ISlashBladeState state = stack.getCapability(BladeStateCapabilityProvider.CAP).orElseThrow(IllegalStateException::new);

        String renderTarget;
        if(types.contains(SwordType.Broken)){
            renderTarget = "item_damaged";
        }else if(!types.contains(SwordType.NoScabbard)){
            renderTarget = "item_blade";
        }else{
            renderTarget = "item_bladens";
        }
        
        if(state.getModel().isPresent() && state.getTexture().isPresent()){
            WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().get());
            ResourceLocation texture = state.getTexture().get();

            BladeRenderState.renderOverrided(stack, obj, renderTarget, texture, matrixStack, bufferIn, lightIn);
            BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", texture, matrixStack, bufferIn, lightIn);
        }else if(objectOptional.isPresent()){
            JsonObject object = objectOptional.get();

            WavefrontObject model = TicEXSlashBladeAssetsLoader.loadSlashBladeModel(object);
            java.util.List<Pair<ResourceLocation, MaterialVariantId>> textures = TicEXSlashBladeAssetsLoader.loadSlashBladeTexture(object, materials);

            TicEXBladeRenderState.renderOverrided(stack, model, renderTarget, textures, matrixStack, bufferIn, lightIn);
            TicEXBladeRenderState.renderOverridedLuminous(stack, model, renderTarget + "_luminous", textures, matrixStack, bufferIn, lightIn);
        } else {
            ResourceLocation textureLocation = state.getTexture().orElseGet(() -> BladeModelManager.resourceDefaultTexture);
            WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().orElse(null));

            BladeRenderState.renderOverrided(stack, obj, renderTarget, textureLocation, matrixStack, bufferIn, lightIn);
            BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
        }

        if(renderDurability){

            WavefrontObject durabilityModel = BladeModelManager.getInstance().getModel(BladeModelManager.resourceDurabilityModel);

            float durability = stack.getCapability(ItemSlashBlade.BLADESTATE).map(s->s.getDurabilityForDisplay()).orElse(0.0f);
            matrixStack.translate(0.0F, 0.0F, 0.1f);

            if(BladeModel.user != null && BladeModel.user.getMainHandItem().equals(stack)){

                BladeRenderState.setCol(new Color(0xEEEEEE));
                BladeRenderState.renderOverrided(stack, durabilityModel, "base", BladeModelManager.resourceDurabilityTexture, matrixStack, bufferIn, lightIn);
                matrixStack.translate(0.0F, 0.0F, 0.1f);
                BladeRenderState.setCol(Color.black);
                BladeRenderState.renderOverrided(stack, durabilityModel, "color_r", BladeModelManager.resourceDurabilityTexture, matrixStack, bufferIn, lightIn);
                BladeRenderState.resetCol();
            }else{
                Color aCol = new Color(0.25f,0.25f,0.25f,1.0f);
                Color bCol = new Color(0xA52C63);
                int r = 0xFF & (int)Mth.lerp(aCol.getRed(), bCol.getRed(),durability);
                int g = 0xFF & (int)Mth.lerp(aCol.getGreen(), bCol.getGreen(),durability);
                int b = 0xFF & (int)Mth.lerp(aCol.getBlue(), bCol.getBlue(),durability);

                BladeRenderState.setCol(new Color(r,g,b));
                BladeRenderState.renderOverrided(stack, durabilityModel, "base", BladeModelManager.resourceDurabilityTexture, matrixStack, bufferIn, lightIn);


                boolean isBroken = types.contains(SwordType.Broken);
                matrixStack.translate(0.0F, 0.0F, -2.0f * durability);
                BladeRenderState.renderOverrided(stack, durabilityModel, isBroken ? "color_r" : "color", BladeModelManager.resourceDurabilityTexture, matrixStack, bufferIn, lightIn);
                BladeRenderState.resetCol();
            }
        }
    }

    

    @SuppressWarnings("null")
    private void renderModel(ItemStack stack, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn){

        ToolStack tool = ToolStack.from(stack);
        java.util.List<MaterialVariant> materials = tool.getMaterials().getList();

        float scale = 0.003125f;
        matrixStack.scale(scale, scale, scale);
        float defaultOffset = 130;
        matrixStack.translate(defaultOffset, 0, 0);

        EnumSet<SwordType> types = SwordType.from(stack);

        Optional<JsonObject>objectOptional = TicEXSlashBladeAssetsLoader.loadSlashBladeAssets(modelLocation);
        ISlashBladeState state = stack.getCapability(BladeStateCapabilityProvider.CAP).orElseThrow(IllegalStateException::new);

        Vec3 bladeOffset = Vec3.ZERO;
            float bladeOffsetRot =0;
            float bladeOffsetBaseRot = -3;
            Vec3 sheathOffset = Vec3.ZERO;
            float sheathOffsetRot =0;
            float sheathOffsetBaseRot = -3;
            boolean vFlip = false;
            boolean hFlip = false;
            boolean hasScabbard = !types.contains(SwordType.NoScabbard);

            if(stack.isFramed()){
                if(stack.getFrame() instanceof BladeStandEntity){
                    BladeStandEntity stand = (BladeStandEntity) stack.getFrame();
                    Item type = stand.currentType;

                    Pose pose = stand.getPose();
                    switch (pose.ordinal()){
                        case 0:
                            vFlip = false;
                            hFlip = false;
                            break;
                        case 1:
                            vFlip = true;
                            hFlip = false;
                            break;
                        case 2:
                            vFlip = true;
                            hFlip = true;
                            break;
                        case 3:
                            vFlip = false;
                            hFlip = true;
                            break;
                        case 4:
                            vFlip = false;
                            hFlip = false;
                            hasScabbard = false;
                            break;
                        case 5:
                            vFlip = false;
                            hFlip = true;
                            hasScabbard = false;
                            break;
                    }

                    if(type == SBItems.bladestand_1) {
                        bladeOffset = Vec3.ZERO;
                        sheathOffset = Vec3.ZERO;
                    }else if(type == SBItems.bladestand_2){
                        bladeOffset = new Vec3(0,21.5f,0);
                        if(hFlip){
                            sheathOffset = new Vec3(-40,-27,0);
                        }else{
                            sheathOffset = new Vec3(40,-27,0);
                        }
                        sheathOffsetBaseRot = -4;
                    }else if(type == SBItems.bladestand_v){
                        bladeOffset = new Vec3(-100,230,0);
                        sheathOffset = new Vec3(-100,230,0);
                        bladeOffsetRot = 80;
                        sheathOffsetRot = 80;
                    }else if(type == SBItems.bladestand_s){
                        if(hFlip){
                            bladeOffset = new Vec3(60,-25,0);
                            sheathOffset = new Vec3(60,-25,0);
                        }else{
                            bladeOffset = new Vec3(-60,-25,0);
                            sheathOffset = new Vec3(-60,-25,0);
                        }
                    }else if(type == SBItems.bladestand_1w){
                        bladeOffset = Vec3.ZERO;
                        sheathOffset = Vec3.ZERO;
                    }else if(type == SBItems.bladestand_2w){
                        bladeOffset = new Vec3(0,21.5f,0);
                        if(hFlip){
                            sheathOffset = new Vec3(-40,-27,0);
                        }else{
                            sheathOffset = new Vec3(40,-27,0);
                        }
                        sheathOffsetBaseRot = -4;
                    }
                }
            }

        
            try(MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                String renderTarget;
                if(types.contains(SwordType.Broken))
                    renderTarget = "blade_damaged";
                else
                    renderTarget = "blade";

                matrixStack.translate(bladeOffset.x, bladeOffset.y, bladeOffset.z);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(bladeOffsetRot));


                if(vFlip) {
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                    matrixStack.translate(0, -15,0);

                    matrixStack.translate(0, 5, 0);
                }

                if (hFlip) {
                    double offset = defaultOffset;
                    matrixStack.translate(-offset, 0,0);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
                    matrixStack.translate(offset, 0,0);
                }

                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(bladeOffsetBaseRot));

                if(state.getModel().isPresent() && state.getTexture().isPresent()){
                    WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().get());
                    ResourceLocation texture = state.getTexture().get();
        
                    BladeRenderState.renderOverrided(stack, obj, renderTarget, texture, matrixStack, bufferIn, lightIn);
                    BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", texture, matrixStack, bufferIn, lightIn);
                }else if(objectOptional.isPresent()){
                    JsonObject object = objectOptional.get();
        
                    WavefrontObject model = TicEXSlashBladeAssetsLoader.loadSlashBladeModel(object);
                    java.util.List<Pair<ResourceLocation, MaterialVariantId>> textures = TicEXSlashBladeAssetsLoader.loadSlashBladeTexture(object, materials);
        
                    TicEXBladeRenderState.renderOverrided(stack, model, renderTarget, textures, matrixStack, bufferIn, lightIn);
                    TicEXBladeRenderState.renderOverridedLuminous(stack, model, renderTarget + "_luminous", textures, matrixStack, bufferIn, lightIn);
                }else {
                    ResourceLocation textureLocation = state.getTexture().orElseGet(() -> BladeModelManager.resourceDefaultTexture);
                    WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().orElse(null));
        
                    BladeRenderState.renderOverrided(stack, obj, renderTarget, textureLocation, matrixStack, bufferIn, lightIn);
                    BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                }
            }

            if(hasScabbard){
                try(MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                    String renderTarget = "sheath";
    
                    matrixStack.translate(sheathOffset.x, sheathOffset.y, sheathOffset.z);
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(sheathOffsetRot));
    
    
                    if(vFlip) {
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                        matrixStack.translate(0, -15,0);
    
                        matrixStack.translate(0, 5, 0);
                    }
    
                    if (hFlip) {
                        double offset = defaultOffset;
                        matrixStack.translate(-offset, 0,0);
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
                        matrixStack.translate(offset, 0,0);
                    }
    
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(sheathOffsetBaseRot));
                    
                    if(state.getModel().isPresent() && state.getTexture().isPresent()){
                        WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().get());
                        ResourceLocation texture = state.getTexture().get();
            
                        BladeRenderState.renderOverrided(stack, obj, renderTarget, texture, matrixStack, bufferIn, lightIn);
                        BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", texture, matrixStack, bufferIn, lightIn);
                    }else if(objectOptional.isPresent()){

                        JsonObject object = objectOptional.get();
            
                        WavefrontObject model = TicEXSlashBladeAssetsLoader.loadSlashBladeModel(object);
                        java.util.List<Pair<ResourceLocation, MaterialVariantId>> textures = TicEXSlashBladeAssetsLoader.loadSlashBladeTexture(object, materials);
            
                        TicEXBladeRenderState.renderOverrided(stack, model, renderTarget, textures, matrixStack, bufferIn, lightIn);
                        TicEXBladeRenderState.renderOverridedLuminous(stack, model, renderTarget + "_luminous", textures, matrixStack, bufferIn, lightIn);
                    }else {
                        ResourceLocation textureLocation = state.getTexture().orElseGet(() -> BladeModelManager.resourceDefaultTexture);
                        WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().orElse(null));
            
                        BladeRenderState.renderOverrided(stack, obj, renderTarget, textureLocation, matrixStack, bufferIn, lightIn);
                        BladeRenderState.renderOverridedLuminous(stack, obj, renderTarget + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                    }
                }
            }
        }
    }
