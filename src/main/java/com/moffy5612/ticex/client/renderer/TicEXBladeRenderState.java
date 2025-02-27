package com.moffy5612.ticex.client.renderer;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.lwjgl.opengl.GL14;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;

import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class TicEXBladeRenderState extends RenderStateShard{
    public static final VertexFormat POSITION_TEX_LMAP_COL_NORMAL = new VertexFormat(ImmutableMap.<String,VertexFormatElement>builder().put("Position",DefaultVertexFormat.ELEMENT_POSITION).put("Color",DefaultVertexFormat.ELEMENT_COLOR).put("UV0",DefaultVertexFormat.ELEMENT_UV0).put("UV2",DefaultVertexFormat.ELEMENT_UV2).put("Normal",DefaultVertexFormat.ELEMENT_NORMAL).put("Padding",DefaultVertexFormat.ELEMENT_PADDING).build());;

    public TicEXBladeRenderState(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        super(p_110161_, p_110162_, p_110163_);
    }

    private static final Color defaultColor = Color.white;
    private static Color col = defaultColor;
    public static void setCol(int rgba){
        setCol(rgba, true);
    }
    public static void setCol(int rgb, boolean hasAlpha){
        setCol(new Color(rgb, hasAlpha));
    }
    public static void setCol(Color value) {
        col = value;
    }

    public static final int MAX_LIGHT = 15728864;

    public static void resetCol() {
        col = defaultColor;
    }

    static public void renderOverrided(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        renderOverrided(stack, model, target, textures, matrixStackIn, bufferIn, packedLightIn, Util.memoize(BladeRenderState::getSlashBladeBlend), true);
    }

    static public void renderOverridedColorWrite(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        renderOverrided(stack, model, target, textures, matrixStackIn, bufferIn, packedLightIn, Util.memoize(BladeRenderState::getSlashBladeBlendColorWrite), true);
    }

    static public void renderOverridedLuminous(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        renderOverrided(stack, model, target, textures, matrixStackIn, bufferIn, packedLightIn, Util.memoize(BladeRenderState::getSlashBladeBlendLuminous), false);
    }
    static public void renderOverridedLuminousDepthWrite(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        renderOverrided(stack, model, target, textures, matrixStackIn, bufferIn, packedLightIn, Util.memoize(BladeRenderState::getSlashBladeBlendLuminousDepthWrite), false);
    }

    static public void renderOverridedReverseLuminous(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        renderOverrided(stack, model, target, textures, matrixStackIn, bufferIn, packedLightIn, Util.memoize(BladeRenderState::getSlashBladeBlendReverseLuminous), false);
    }


    static public void renderOverrided(ItemStack stack, WavefrontObject model, String target, List<Pair<ResourceLocation, MaterialVariantId>> textures, PoseStack  matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Function<ResourceLocation,RenderType> getRenderType, boolean enableEffect){
        for(Pair<ResourceLocation, MaterialVariantId> texture : textures){
            RenderType rt = getRenderType.apply(texture.getFirst());
            VertexConsumer vb = bufferIn.getBuffer(rt);

            Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(texture.getSecond());
            if(optional.isPresent()){
                MaterialRenderInfo info = optional.get();
                setCol(info.getVertexColor());
            }
            
            Face.setCol(col);
            Face.setLightMap(packedLightIn);
            Face.setMatrix(matrixStackIn);
            model.tessellateOnly(vb, new String[]{target});
            if(stack.hasFoil() && enableEffect){
                vb = bufferIn.getBuffer(BladeRenderState.BLADE_GLINT);
                model.tessellateOnly(vb, new String[]{target});
            }

            Face.resetMatrix();
            Face.resetLightMap();
            Face.resetCol();

            Face.resetAlphaOverride();
            Face.resetUvOperator();

            resetCol();
        }
    }

    public static RenderType getSlashBladeBlend(ResourceLocation texture, int index) {

        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                //.setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(true);

        return RenderType.create("slashblade_blend_"  + index, WavefrontObject.POSITION_TEX_LMAP_COL_NORMAL, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }

    public static RenderType getSlashBladeBlendColorWrite(ResourceLocation texture, int index) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                //.setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING)
                .setLightmapState(LIGHTMAP)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(true);
        return RenderType.create("slashblade_blend_write_color_" + index, WavefrontObject.POSITION_TEX_LMAP_COL_NORMAL, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }



    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType getSlashBladeBlendLuminous(ResourceLocation texture, int index) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(PARTICLES_TARGET)
                .setCullState(RenderStateShard.NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                //.setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("ticex_slashblade_blend_luminous_" + index, WavefrontObject.POSITION_TEX_LMAP_COL_NORMAL, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }
    public static RenderType getSlashBladeBlendLuminousDepthWrite(ResourceLocation texture, int index) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(RenderStateShard.PARTICLES_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                //.setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .createCompositeState(false);
        return RenderType.create("ticex_slashblade_blend_luminous_depth_write_" + index, WavefrontObject.POSITION_TEX_LMAP_COL_NORMAL, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }


    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_REVERSE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE
                , GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
    }, () -> {
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType getSlashBladeBlendReverseLuminous(ResourceLocation texture, int index) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(PARTICLES_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                .setTransparencyState(LIGHTNING_REVERSE_TRANSPARENCY)
                //.setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("ticex_slashblade_blend_reverse_luminous_" + index, WavefrontObject.POSITION_TEX_LMAP_COL_NORMAL, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }



    public static RenderType getPlacePreviewBlendLuminous(ResourceLocation texture, int index) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setOutputState(PARTICLES_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                //.setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                //.overlay(OVERLAY_ENABLED)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("ticex_placepreview_blend_luminous_" + index, DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, state);
    }
}
