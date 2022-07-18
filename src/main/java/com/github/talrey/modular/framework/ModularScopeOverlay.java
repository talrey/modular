package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class ModularScopeOverlay {
  public static final IIngameOverlay GETTER = ModularScopeOverlay::render;

  private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = new ResourceLocation("textures/misc/spyglass_scope.png");

  private static final float SCOPE_SCALE = 0.5f;

  public static void tick () {
  }

  public static void render (ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height) {
    Minecraft minecraft = Minecraft.getInstance();
    if (! (
      minecraft.player != null
      && minecraft.player.isUsingItem()
      && IModularTool.hasComponent(minecraft.player.getUseItem(), ItemRegistration.MODIFIER_SCOPED.get())
    )) return;

    RenderSystem.disableDepthTest();
    RenderSystem.depthMask(false);
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE_LOCATION);
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferBuilder = tesselator.getBuilder();
    float f = (float)Math.min(width, height);
    float f1 = Math.min(width/f, height/f) + SCOPE_SCALE;
    float f2 = f * f1;
    float f3 = f * f1;
    float f4 = (width - f2) / 2f;
    float f5 = (height - f3) / 2f;
    float f6 = f4 + f2;
    float f7 = f5 + f3;

    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    bufferBuilder.vertex((double)f4, (double)f7, -90D).uv(0f, 1f).endVertex();
    bufferBuilder.vertex((double)f6, (double)f7, -90D).uv(1f, 1f).endVertex();
    bufferBuilder.vertex((double)f6, (double)f5, -90D).uv(1f, 0f).endVertex();
    bufferBuilder.vertex((double)f4, (double)f5, -90D).uv(0f, 0f).endVertex();
    tesselator.end();

    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    RenderSystem.disableTexture();
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    bufferBuilder.vertex(0D, (double)height, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, (double)height, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex(0D, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex(0D, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, 0D, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex(0D, 0D, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex(0D, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)f4, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)f4, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex(0D, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)f6, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, (double)f7, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)width, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    bufferBuilder.vertex((double)f6, (double)f5, -90D).color(0, 0, 0, 255).endVertex();
    tesselator.end();
    RenderSystem.enableTexture();
    RenderSystem.depthMask(true);
    RenderSystem.enableDepthTest();
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
  }
}
