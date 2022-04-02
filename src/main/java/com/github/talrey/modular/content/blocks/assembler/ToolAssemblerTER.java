package com.github.talrey.modular.content.blocks.assembler;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.ItemStackHandler;

public class ToolAssemblerTER extends TileEntityRenderer<ToolAssemblerTE> {

  private static final Vector2f[] OFFSETS = {
    new Vector2f(0f, 0f),      // core, center
    new Vector2f(0.3f, -0.3f), // handle, left down
    new Vector2f(-0.3f, 0.3f), // function, right up
    new Vector2f(-0.3f, 0f),   // function, right center
    new Vector2f(0f, 0.3f),    // function, center up
    new Vector2f(0.3f, 0f),    // modifier, left center
    new Vector2f(0f, -0.3f),   // modifier, center down
    new Vector2f(-0.3f, -0.3f) // modifier, right down
  };
  private static final Vector2f INVALID_OFFSET = new Vector2f(0.3f, 0.3f); // left up

  public ToolAssemblerTER(TileEntityRendererDispatcher terDispatcher) {
    super(terDispatcher);
  }

  // TODO account for block rotation
  public static int getSlotIndexFromHit (BlockState state, BlockRayTraceResult hit) {
    if (hit.getDirection() != Direction.UP) return -1;
    Vector2f norm = normalizeFaceHit (state, hit);
    if (norm.x < 0.3) {
      if (norm.y < 0.3) {
        return 7; // OFFSETS -.3 -.3
      }
      else if (norm.y > 0.7) {
        return 2; // OFFSETS -.3 +.3
      }
      else return 3; // OFFSETS -.3 0
    }
    else if (norm.x > 0.7) {
      if (norm.y < 0.3) {
        return 1; // OFFSETS +.3 -.3
      }
      else if (norm.y > 0.3) {
        return -1; // OFFSETS +.3 +.3 == INVALID OFFSET
      }
      else return 5; // OFFSETS +.3 0
    }
    else {
      if (norm.y < 0.3) {
        return 6; // OFFSETS 0 -.3
      }
      else if (norm.y > 0.7) {
        return 4; // OFFSETS 0 +.3
      }
      else return 0; // OFFSETS 0 0
    }
  }

  private static Vector2f normalizeFaceHit (BlockState state, BlockRayTraceResult hit) {
    double x = hit.getLocation().x;
    double z = hit.getLocation().z;
    return new Vector2f(
    (float)(x < 0 ? x - Math.floor(x) : x)%1,
    (float)(z < 0 ? z - Math.floor(z) : z)%1
    );
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
