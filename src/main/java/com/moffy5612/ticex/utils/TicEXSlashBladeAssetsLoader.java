package com.moffy5612.ticex.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class TicEXSlashBladeAssetsLoader {
    private static final String PREFIX = "slashblade";

    public static Optional<JsonObject> loadSlashBladeAssets(ResourceLocation modelLocation) {
        return AssetsLoader.loadJSON(modelLocation, PREFIX);
    }

    public static WavefrontObject loadSlashBladeModel(JsonObject object){
        return BladeModelManager.getInstance().getModel(AssetsLoader.resolveModel(object.get("model").getAsString(), PREFIX));
    }

    public static List<Pair<ResourceLocation, MaterialVariantId>> loadSlashBladeTexture(JsonObject object, List<MaterialVariant> materials){
        List<Pair<ResourceLocation, MaterialVariantId>> textures = new ArrayList<>();
        JsonObject texturesObject = object.getAsJsonObject("textures");
        textures.add(new Pair<ResourceLocation,MaterialVariantId>(AssetsLoader.resolveTexture(texturesObject.get("blade").getAsString(), PREFIX), materials.get(0).getVariant()));
        textures.add(new Pair<ResourceLocation,MaterialVariantId>(AssetsLoader.resolveTexture(texturesObject.get("handle").getAsString(), PREFIX), materials.get(1).getVariant()));
        textures.add(new Pair<ResourceLocation,MaterialVariantId>(AssetsLoader.resolveTexture(texturesObject.get("saya").getAsString(), PREFIX), materials.get(2).getVariant()));
        return textures;
    }
}
