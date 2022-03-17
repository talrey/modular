package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.content.TileEntityRegistration;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
      if (handItem instanceof ModularToolComponent) {
        ModularToolComponent mtc = (ModularToolComponent)handItem;

        if (tate.canInsertComponent(mtc)) {
          tate.insertComponent(handStack.split(1));
          return ActionResultType.SUCCESS;
        }
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
