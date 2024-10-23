package com.moffy5612.ticex.client.renderer;

import java.util.List;
import java.util.Optional;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.moffy5612.ticex.items.ToolSlashBlade;
import com.moffy5612.ticex.utils.TicEXSlashBladeAssetsLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ComboState;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXLayerMainBlade extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>{

    @SuppressWarnings("null")
    LazyOptional<MmdPmdModelMc> bladeholder =
            LazyOptional.of(() -> {
                try {
                    return new MmdPmdModelMc(new ResourceLocation(SlashBlade.modid, "model/bladeholder.pmd"));
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                } catch (MmdException mmdExceptionModel) {
                    mmdExceptionModel.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return null;
            });

    LazyOptional<MmdMotionPlayerGL2> motionPlayer =
            LazyOptional.of(() -> {
                MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();;

                bladeholder.ifPresent(pmd -> {
                    try {
                        mmp.setPmd(pmd);
                    } catch (MmdException mmdExceptionPlayer) {
                        mmdExceptionPlayer.printStackTrace();
                    }
                });

                return mmp;
            });

    public TicEXLayerMainBlade(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> entityRendererIn) {
        super(entityRendererIn);
    }

    @SuppressWarnings("null")
    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float motionYOffset = 1.5f;
        double motionScale = 1.5 / 12.0;
        double modelScaleBase = 0.0078125F;

        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);

        if(stack.isEmpty() || !(stack.getItem() instanceof ToolSlashBlade)) return;

        LazyOptional<ISlashBladeState> state = stack.getCapability(CapabilitySlashBlade.BLADESTATE);
        state.ifPresent(s -> {

            motionPlayer.ifPresent(mmp ->
            {
                ComboState combo = s.getComboSeq();
                
                double time = TimeValueHelper.getMSecFromTicks(Math.max(0, entity.level.getGameTime() - s.getLastActionTime()) + partialTicks);

                while(combo != ComboState.NONE && combo.getTimeoutMS() < time){
                    time -= combo.getTimeoutMS();

                    combo = combo.getNextOfTimeout();
                }
                if(combo == ComboState.NONE){
                    combo = s.getComboRoot();
                }

                MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());

                double maxSeconds = 0;
                try {
                    mmp.setVmd(motion);
                    maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                double start = TimeValueHelper.getMSecFromFrames(combo.getStartFrame());
                double end = TimeValueHelper.getMSecFromFrames(combo.getEndFrame());
                double span = Math.abs(end - start);

                span = Math.min(maxSeconds, span);

                boolean isRoop = combo.getRoop();
                if (isRoop) {
                    time = time % span;
                }
                time = Math.min(span, time);

                time = start + time;

                try {
                    mmp.updateMotion((float)time);
                } catch (MmdException e) {
                    e.printStackTrace();
                }


                try(MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)){

                    UserPoseOverrider.invertRot(matrixStack,entity,partialTicks);

                    
                    matrixStack.translate(0, motionYOffset, 0);

                    matrixStack.scale((float)motionScale, (float)motionScale, (float)motionScale);


                    
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));


                    

                    try(MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)){
                        int idx = mmp.getBoneIndexByName("hardpointA");

                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);

                            Matrix4f mat = new Matrix4f(buf);
                            mat.transpose();

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().multiply(mat);
                            matrixStack.scale(-1, 1, 1);
                        }

                        float modelScale = (float)(modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);

                        String part;
                        if(s.isBroken()){
                            part = "blade_damaged";
                        }else{
                            part = "blade";
                        }

                        ToolSlashBlade sb = (ToolSlashBlade)stack.getItem();
                        ToolStack tool = ToolStack.from(stack);
                        List<MaterialVariant> materials = tool.getMaterials().getList();
                        Optional<JsonObject> objectOptional = TicEXSlashBladeAssetsLoader.loadSlashBladeAssets(sb.getDefaultModelsLocation());
                        if(s.getModel().isPresent() && s.getTexture().isPresent()){
                            WavefrontObject obj = BladeModelManager.getInstance().getModel(s.getModel().get());
                            ResourceLocation texture = s.getTexture().get();

                            BladeRenderState.renderOverrided(stack, obj, part, texture, matrixStack, bufferIn, lightIn);
                            BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", texture, matrixStack, bufferIn, lightIn);
                        }else if(objectOptional.isPresent()){
                            JsonObject object = objectOptional.get();
                            WavefrontObject obj = TicEXSlashBladeAssetsLoader.loadSlashBladeModel(object);
                            List<Pair<ResourceLocation, MaterialVariantId>> textures = TicEXSlashBladeAssetsLoader.loadSlashBladeTexture(object, materials);

                            TicEXBladeRenderState.renderOverrided(stack, obj, part, textures, matrixStack, bufferIn, lightIn);
                            TicEXBladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textures, matrixStack, bufferIn, lightIn);

                        } else {
                            ResourceLocation textureLocation = s.getTexture().orElseGet(() -> BladeModelManager.resourceDefaultTexture);
                            WavefrontObject obj = BladeModelManager.getInstance().getModel(s.getModel().orElse(null));

                            BladeRenderState.renderOverrided(stack, obj, part, textureLocation, matrixStack, bufferIn, lightIn);
                            BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                        }
                    }
                    try(MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)){
                        int idx = mmp.getBoneIndexByName("hardpointB");

                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);

                            Matrix4f mat = new Matrix4f(buf);
                            mat.transpose();

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().multiply(mat);
                            matrixStack.scale(-1, 1, 1);
                        }


                        float modelScale = (float)(modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);

                        ToolSlashBlade sb = (ToolSlashBlade)stack.getItem();
                        ToolStack tool = ToolStack.from(stack);
                        List<MaterialVariant> materials = tool.getMaterials().getList();

                        boolean isDefault = !(s.getModel().isPresent() && s.getTexture().isPresent());
                        Optional<JsonObject> objectOptional = TicEXSlashBladeAssetsLoader.loadSlashBladeAssets(sb.getDefaultModelsLocation());
                        if(isDefault && objectOptional.isPresent()){
                            JsonObject object = objectOptional.get();
                            WavefrontObject obj = TicEXSlashBladeAssetsLoader.loadSlashBladeModel(object);
                            List<Pair<ResourceLocation, MaterialVariantId>> textures = TicEXSlashBladeAssetsLoader.loadSlashBladeTexture(object, materials);

                            TicEXBladeRenderState.renderOverrided(stack, obj, "sheath", textures, matrixStack, bufferIn, lightIn);
                            TicEXBladeRenderState.renderOverridedLuminous(stack, obj, "sheath_luminous", textures, matrixStack, bufferIn, lightIn);

                        } else {
                            ResourceLocation textureLocation = s.getTexture().orElseGet(() -> BladeModelManager.resourceDefaultTexture);
                            WavefrontObject obj = BladeModelManager.getInstance().getModel(s.getModel().orElse(null));

                            BladeRenderState.renderOverrided(stack, obj, "sheath", textureLocation, matrixStack, bufferIn, lightIn);
                            BladeRenderState.renderOverridedLuminous(stack, obj, "sheath_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                        }
                    }
                }

            });

        });
    }
}
