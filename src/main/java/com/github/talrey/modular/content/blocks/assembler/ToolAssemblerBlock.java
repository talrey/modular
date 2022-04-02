package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.TileEntityRegistration;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ToolAssemblerBlock extends Block {

  public ToolAssemblerBlock(Properties props) {
    super(props);
  }

  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayResult) {
    TileEntity te       = world.getBlockEntity(pos);
    ItemStack handStack = player.getItemInHand(hand);
    Item handItem       = handStack.getItem();

    if (te instanceof ToolAssemblerTE) {
      ToolAssemblerTE tate = (ToolAssemblerTE)te;

      if (player.isShiftKeyDown()) { // sneaking
        if (rayResult.getDirection() == Direction.UP) {
          int index = ToolAssemblerTER.getSlotIndexFromHit(state, rayResult);
          if (index >= 0) {
            if (!tate.giveSlot(player, index)) tate.ejectSlot(index);
          }
        }
        else tate.giveOrEjectAll(player);
      }
      else if (handItem instanceof ModularToolComponent) {
        ModularToolComponent mtc = (ModularToolComponent)handItem;

        if (tate.canInsertComponent(mtc)) {
          tate.insertComponent(handStack.split(1));
          return ActionResultType.SUCCESS;
        }
        else player.displayClientMessage(new StringTextComponent("Cannot insert more of that type of component"), true);
      }
      else if (handItem instanceof IModularTool) {
        if (tate.isEmpty()) {
          ModularToolComponent[] parts = IModularTool.getAllComponents(handStack);
          for (ModularToolComponent mtc : parts) {
            if (mtc != null) {
              tate.insertComponent(mtc.onRemoval(handStack));
            }
          }
          player.setItemInHand(hand, ItemStack.EMPTY);
        }
        else player.displayClientMessage(new StringTextComponent("Tool assembler is occupied by parts already!"), true);
        return ActionResultType.SUCCESS;
      }
      else if (handStack.isEmpty()) {
        player.setItemInHand(hand, tate.tryAssembleTool());
        return ActionResultType.SUCCESS;
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return TileEntityRegistration.TOOL_ASM.create();
  }
}
