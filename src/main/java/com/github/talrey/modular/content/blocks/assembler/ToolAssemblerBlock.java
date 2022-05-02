package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.content.BlockEntityRegistration;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import com.github.talrey.modular.framework.capability.CapabilityNotPresentException;
import com.github.talrey.modular.framework.capability.IModularToolSystem;
import com.github.talrey.modular.framework.capability.ModularToolCapability;
import com.github.talrey.modular.framework.network.ModularToolsPacketHandler;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

public class ToolAssemblerBlock extends BaseEntityBlock {

  public ToolAssemblerBlock(Properties props) {
    super(props);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayResult) {
    BlockEntity te       = world.getBlockEntity(pos);
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
          return InteractionResult.SUCCESS;
        }
        else player.displayClientMessage(new TextComponent("Cannot insert more of that type of component"), true);
      }
      else if (handItem instanceof IModularTool) {
        if (tate.isEmpty()) {
          if (!world.isClientSide) {
            IModularToolSystem cache = handStack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);
            ModularToolComponent[] parts = cache.getComponents();
            for (ModularToolComponent mtc : parts) {
              if (mtc == null) continue;
              tate.insertComponent(cache.removeComponent(mtc));
            }
            ModularToolsPacketHandler.updateToolAssemblerClientside(tate.serializeNBT(), world, pos);
          }
          player.setItemInHand(hand, ItemStack.EMPTY);
          return InteractionResult.SUCCESS;
        }
      }
      else if (handStack.isEmpty()) {
        player.setItemInHand(hand, tate.tryAssembleTool());
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return BlockEntityRegistration.TOOL_ASM.create(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }
}
