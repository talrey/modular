package com.github.talrey.modular.content.blocks.assembler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;
import net.minecraftforge.items.ItemStackHandler;

public class ToolAssemblerTER implements BlockEntityRenderer<ToolAssemblerTE> {

  private static final Vec2[] OFFSETS = {
    new Vec2(0f, 0f),      // core, center
    new Vec2(0.3f, -0.3f), // handle, left down
    new Vec2(-0.3f, 0.3f), // function, right up
    new Vec2(-0.3f, 0f),   // function, right center
    new Vec2(0f, 0.3f),    // function, center up
    new Vec2(0.3f, 0f),    // modifier, left center
    new Vec2(0f, -0.3f),   // modifier, center down
    new Vec2(-0.3f, -0.3f) // modifier, right down
  };
  private static final Vec2 INVALID_OFFSET = new Vec2(0.3f, 0.3f); // left up

  public ToolAssemblerTER() {
  }

  // TODO account for block rotation
  public static int getSlotIndexFromHit (BlockState state, BlockHitResult hit) {
    if (hit.getDirection() != Direction.UP) return -1;
    Vec2 norm = normalizeFaceHit (state, hit);
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

  private static Vec2 normalizeFaceHit (BlockState state, BlockHitResult hit) {
    double x = hit.getLocation().x;
    double z = hit.getLocation().z;
    return new Vec2(
    (float)(x < 0 ? x - Math.floor(x) : x)%1,
    (float)(z < 0 ? z - Math.floor(z) : z)%1
    );
  }

  @Override
  public void render(ToolAssemblerTE tate, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    ItemStackHandler inventory = tate.getInventory();
    Direction facing = Direction.NORTH;

    for (int slot=0; slot < inventory.getSlots(); slot++) {
      ItemStack stack = inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        matrix.pushPose();
        Vec2 offset = (slot < OFFSETS.length ? OFFSETS[slot] : INVALID_OFFSET);

        // center above block, rotate flat, align, resize
        matrix.translate(0.5D, 1.02D, 0.5D);
        matrix.mulPose(Vector3f.YP.rotationDegrees( -facing.toYRot() ));
        matrix.mulPose(Vector3f.XP.rotationDegrees(90f));
        matrix.translate(offset.x, offset.y, 0D);
        matrix.scale(0.375f, 0.375f, 0.375f);

        if (tate.getLevel() != null) {
          int MYSTERY_NUMBER = 1; // TODO figure out what the heck this is
          Minecraft.getInstance().getItemRenderer().renderStatic(
            stack, ItemTransforms.TransformType.FIXED, LevelRenderer.getLightColor(tate.getLevel(), tate.getBlockPos().above()),
            combinedOverlay, matrix, buffer, MYSTERY_NUMBER
          );
        }

        matrix.popPose();
      }
    }
  }
}
