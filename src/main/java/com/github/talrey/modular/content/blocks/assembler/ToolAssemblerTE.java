package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class ToolAssemblerTE extends TileEntity {

  public static final int INVENTORY_SIZE = 6;

  private final ItemStackHandler inv;

  public ToolAssemblerTE(TileEntityType<?> type) {
    super(type);
    inv = new ItemStackHandler(INVENTORY_SIZE) {
      @Override
      protected void onContentsChanged (int slot) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
          // update the output tool
        }
        inventoryChanged();
      }
    };
  }

  private int getAvailableSlot (ModularToolComponent mtc) {
    int slot = -1;
    switch (mtc.getType()) {
      case CORE -> slot = (inv.getStackInSlot(0).isEmpty() ? 0 : -1);
      case HANDLE -> slot = (inv.getStackInSlot(1).isEmpty() ? 1 : -1);
      case FUNCTION -> slot = (inv.getStackInSlot(2).isEmpty() ? 2 : inv.getStackInSlot(3).isEmpty() ? 3 : -1);
      case MODIFIER -> slot = (inv.getStackInSlot(4).isEmpty() ? 4 : inv.getStackInSlot(5).isEmpty() ? 5 : -1);
    }
    ModularToolsMod.LOGGER.debug("Available slot at " + slot);
    return slot;
  }

  public boolean isEmpty () {
    for (int slot=0; slot < INVENTORY_SIZE; slot++) {
      if (!inv.getStackInSlot(slot).isEmpty()) return false;
    }
    return true;
  }

  protected boolean canInsertComponent (ModularToolComponent mtc) {
    return getAvailableSlot(mtc) >= 0;
  }

  protected void insertComponent (ItemStack stack) {
    if (stack.getItem() instanceof ModularToolComponent) {
      if (!canInsertComponent((ModularToolComponent) stack.getItem())) return; // safety check
      inv.setStackInSlot(getAvailableSlot((ModularToolComponent) stack.getItem()), stack);
    }
  }

  protected boolean isValidTool () {
    // must have a core, handle, and 1 function at minimum
    return
      (!inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof ModularToolComponent) &&
      (!inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(1).getItem() instanceof ModularToolComponent) &&
      (!inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(2).getItem() instanceof ModularToolComponent);
  }

  protected ItemStack tryAssembleTool () {
    ItemStack out = ItemStack.EMPTY;
    if (isValidTool()) {
      out = new ItemStack((Item)ItemRegistration.getModularTool(inv.getStackInSlot(2)));
      for (int slot=0; slot < INVENTORY_SIZE; slot++) {
        ItemStack part = inv.getStackInSlot(slot);
        if (part.isEmpty()) continue;
        out = IModularTool.addModule(out, part);
        inv.setStackInSlot(slot, ItemStack.EMPTY);
      }
    }
    return out;
  }

  public void inventoryChanged () {
    super.setChanged();
    if (level != null) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }
  }

  protected ItemStackHandler getInventory () {
    return this.inv;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    super.deserializeNBT(nbt);
  }

  @Override
  public CompoundNBT serializeNBT() {
    return super.serializeNBT();
  }

  @Override
  public void load(BlockState state, CompoundNBT cnbt) {
    super.load(state, cnbt);
  }

  @Override
  public CompoundNBT save(CompoundNBT cnbt) {
    return super.save(cnbt);
  }
}
