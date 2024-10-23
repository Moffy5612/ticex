package com.moffy5612.ticex.utils;

import java.util.Optional;

import java.io.InputStreamReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moffy5612.ticex.TicEX;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class AssetsLoader {
    public static Optional<JsonObject> loadJSON(ResourceLocation modelLocation, String prefix){
        try{
            Gson gson = new Gson();
            ResourceLocation resolved = new ResourceLocation(modelLocation.getNamespace(), prefix+"/"+modelLocation.getPath()+".json");
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(resolved);
            JsonObject object = gson.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
            return Optional.of(object);
        }catch(IOException e){
            if(e.getStackTrace().length > 0){
                TicEX.LOGGER.warn(e.getMessage() + e.getStackTrace()[0]);
            } else {
                TicEX.LOGGER.warn(e.getMessage());
            }
            
            return Optional.empty();
        }
    }

    public static ResourceLocation resolveModel(String path, String prefix){
        ResourceLocation preModelLocation = new ResourceLocation(path);
        return new ResourceLocation(preModelLocation.getNamespace(), prefix+"/"+preModelLocation.getPath()+".obj");
    }

    public static ResourceLocation resolveTexture(String path, String prefix){
        ResourceLocation preTextureLocation = new ResourceLocation(path);
        return new ResourceLocation(preTextureLocation.getNamespace(), prefix+"/"+preTextureLocation.getPath()+".png");
    }
}
