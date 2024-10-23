package com.moffy5612.ticex.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import codechicken.lib.render.buffer.BakedQuadVertexBuilder;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


public class TicEXSlashBladeModelGeometry implements IMultipartModelGeometry<TicEXSlashBladeModelGeometry>{

    private List<TicEXSlashBladePart> parts = new ArrayList<>();
    private WavefrontObject obj;

    public static final Loader LOADER = new Loader();

    public TicEXSlashBladeModelGeometry(ResourceLocation modelLocation){
        obj = BladeModelManager.getInstance().getModel(modelLocation);
        obj.groupObjects.forEach(group -> {
            parts.add(new TicEXSlashBladePart(group));
        });
    }

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
        return parts;
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        for(TicEXSlashBladePart part : parts){
            if(part.name().equals(name)){
                return Optional.of(part);
            }
        }
        return Optional.empty();
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides,
            ResourceLocation modelLocation) {
        Overrides newOverrides = new Overrides(owner, bakery, spriteGetter, modelTransform, modelLocation);
        return IMultipartModelGeometry.super.bake(owner, bakery, spriteGetter, modelTransform, newOverrides, modelLocation);
    }

    private class Overrides extends ItemOverrides {
        
        private ModelBakery bakery;
        private IModelConfiguration owner;
        private Function<Material, TextureAtlasSprite> spriteGetter;
        private ModelState modelTransform;
        private ResourceLocation modelLocation;

        public Overrides(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation){
            this.owner = owner;
            this.bakery = bakery;
            this.spriteGetter = spriteGetter;
            this.modelTransform = modelTransform;
            this.modelLocation = modelLocation;
        }

        @SuppressWarnings("null")
        @Override
        @Nullable
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level,
                @Nullable LivingEntity entity, int seed) {
            TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));
            IModelBuilder<?> builder = IModelBuilder.of(owner, this, particle);
            ToolStack tool = ToolStack.from(stack);
            List<MaterialVariant> materials = tool.getMaterials().getList();
            for(int i = 0; i < materials.size(); i++){
                MaterialVariant material = materials.get(i);
                Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material.getVariant());
                if(optional.isPresent()){
                    MaterialRenderInfo info = optional.get();
                    TintedSprite sprite;
                    if(i == 0) {
                        sprite = info.getSprite(owner.resolveTexture("blade"), spriteGetter);
                    } else if(i == 1) {
                        sprite = info.getSprite(owner.resolveTexture("handle"), spriteGetter);
                    } else {
                        sprite = info.getSprite(owner.resolveTexture("saya"), spriteGetter);
                    }

                    getParts().stream().filter(part -> owner.getPartVisibility(part))
                    .forEach(part -> {
                        if(part instanceof TicEXSlashBladePart){
                            ((TicEXSlashBladePart)part).addQuadsWithMaterial(owner, builder, bakery, spriteGetter, modelTransform, modelLocation, sprite);
                        }
                    });
                }
            } 
            return new BladeModel(builder.build(), bakery);    
        }
    }

    private static class Loader implements IModelLoader<TicEXSlashBladeModelGeometry>{

        @SuppressWarnings("null")
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            
        }

        @SuppressWarnings("null")
        @Override
        public TicEXSlashBladeModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            ResourceLocation modelLocation = new ResourceLocation(GsonHelper.getAsString(modelContents, "model"));
            return new TicEXSlashBladeModelGeometry(modelLocation);
        }
    }

    private class TicEXSlashBladePart implements IModelGeometryPart{

        private GroupObject group;

        public TicEXSlashBladePart(GroupObject group){
            this.group = group;
        }

        @Override
        public String name() {
            return group.name;
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                ResourceLocation modelLocation) {
            
                group.faces.forEach(face -> {
                    
                });
        }

        public void addQuadsWithMaterial(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
        Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
        ResourceLocation modelLocation, TintedSprite materialSprite){
            group.faces.forEach(face -> {
                BakedQuadVertexBuilder builder = new BakedQuadVertexBuilder();
                face.addFaceForRender(builder);
                builder.sprite(materialSprite.sprite());
                builder.color(materialSprite.color());
                
                builder.bake().forEach(quad -> {
                    modelBuilder.addGeneralQuad(quad);
                });
            });
        }
    }
}
