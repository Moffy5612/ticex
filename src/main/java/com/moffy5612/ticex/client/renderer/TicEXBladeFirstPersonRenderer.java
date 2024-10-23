package com.moffy5612.ticex.client.renderer;

import com.moffy5612.ticex.items.ToolSlashBlade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TicEXBladeFirstPersonRenderer {
    public static final TicEXBladeFirstPersonRenderer INSTANCE = new TicEXBladeFirstPersonRenderer();

    private TicEXLayerMainBlade layer = null;

    @SuppressWarnings("null")
    public TicEXBladeFirstPersonRenderer(){
        Minecraft mc = Minecraft.getInstance();

        EntityRenderer<? super LocalPlayer> renderer = mc.getEntityRenderDispatcher().getRenderer(mc.player);
        if(renderer instanceof PlayerRenderer)
            layer = new TicEXLayerMainBlade((PlayerRenderer)renderer);
    }

    public TicEXLayerMainBlade getLayer(){
        return this.layer;
    }

    public void setLayer(TicEXLayerMainBlade layer){
        this.layer = layer;
    }

    @SuppressWarnings("null")
    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn){
        if(layer == null)
            return;
        
        Minecraft mc = Minecraft.getInstance();
        boolean flag = mc.getCameraEntity() instanceof LivingEntity && ((LivingEntity) mc.getCameraEntity()).isSleeping();
        if (!(mc.options.getCameraType() == CameraType.FIRST_PERSON && !flag && !mc.options.hideGui && !mc.gameMode.isAlwaysFlying())) {
            return;
        }
        LocalPlayer player = mc.player;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ToolSlashBlade)) return;

        try(MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)){
            PoseStack.Pose me = matrixStack.last();
            me.pose().setIdentity();
            me.normal().setIdentity();

            matrixStack.translate(0.0f, 0.0f, -0.5f);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            matrixStack.scale(1.2F, 1.0F, 1.0F);

            //no sync pitch
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-mc.player.getXRot()));

            //layer.disableOffhandRendering();
            float partialTicks = mc.getFrameTime();
            layer.render(matrixStack, bufferIn, combinedLightIn, mc.player, 0, 0, partialTicks, 0, 0, 0);
        }
    }
}
