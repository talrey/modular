package com.github.talrey.modular.content.blocks.assembler;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.system.CallbackI;

public class ToolAssemblerTER extends TileEntityRenderer<ToolAssemblerTE> {

  private static final Vector2f[] OFFSETS = {
    new Vector2f(0f, 0f),
    new Vector2f(0.3f, -0.3f),
    new Vector2f(0f, 0.3f),
    new Vector2f(-0.3f, 0f),
    new Vector2f(0.3f, 0f),
    new Vector2f(0f, -0.3f)
  };
  private static final Vector2f INVALID_OFFSET = new Vector2f(-0.3f, -0.3f);

  public ToolAssemblerTER(TileEntityRendererDispatcher terDispatcher) {
    super(terDispatcher);
  }

  @Override
  public void render(ToolAssemblerTE tate, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    ItemStackHandler inventory = tate.getInventory();
    Direction facing = Direction.NORTH;

    for (int slot=0; slot < inventory.getSlots(); slot++) {
      ItemStack stack = inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        matrix.pushPose();
        Vector2f offset = (slot < OFFSETS.length ? OFFSETS[slot] : INVALID_OFFSET);

        // center above block, rotate flat, align, resize
        matrix.translate(0.5D, 1.02D, 0.5D);
        matrix.mulPose(Vector3f.YP.rotationDegrees( -facing.toYRot() ));
        matrix.mulPose(Vector3f.XP.rotationDegrees(90f));
        matrix.translate(offset.x, offset.y, 0D);
        matrix.scale(0.375f, 0.375f, 0.375f);

        if (tate.getLevel() != null) {
          Minecraft.getInstance().getItemRenderer().renderStatic(
            stack, ItemCameraTransforms.TransformType.FIXED, WorldRenderer.getLightColor(tate.getLevel(), tate.getBlockPos().above()), combinedOverlay, matrix, buffer
          );
        }

        matrix.popPose();
      }
    }
  }
}
