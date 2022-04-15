package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class ToolAssemblerTE extends TileEntity {

  public static final int INVENTORY_SIZE = 8;

  private final ItemStackHandler inv;
  private static final Vector3f EJECT_POS = new Vector3f(0.5f, 1f, 0.5f);

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
      case CORE:     slot = (inv.getStackInSlot(0).isEmpty() ? 0 : -1); break;
      case HANDLE:   slot = (inv.getStackInSlot(1).isEmpty() ? 1 : -1); break;
      case FUNCTION: slot = (inv.getStackInSlot(2).isEmpty() ? 2 : inv.getStackInSlot(3).isEmpty() ? 3 : inv.getStackInSlot(4).isEmpty() ? 4 : -1); break;
      case MODIFIER: slot = (inv.getStackInSlot(5).isEmpty() ? 5 : inv.getStackInSlot(6).isEmpty() ? 6 : inv.getStackInSlot(7).isEmpty() ? 7 : -1); break;
    }
    //ModularToolsMod.LOGGER.debug("Available slot at " + slot);
    return slot;
  }

  public boolean isEmpty () {
    for (int slot=0; slot < INVENTORY_SIZE; slot++) {
      if (!inv.getStackInSlot(slot).isEmpty()) return false;
    }
    return true;
  }

  protected boolean canInsertComponent (ModularToolComponent mtc) {
    boolean noDupes = true;
    for (int slot=0; slot < INVENTORY_SIZE; slot++) {
      ItemStack stack = inv.getStackInSlot(slot);
      if (stack.isEmpty()) continue;
      if (ItemRegistration.getIndexOfMTC((ModularToolComponent)stack.getItem()) == ItemRegistration.getIndexOfMTC(mtc)) {
        noDupes = false; // disallow duplicates
        break;
      }
    }
    return noDupes && (getAvailableSlot(mtc) >= 0);
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
      out.setDamageValue(inv.getStackInSlot(2).getDamageValue());
      for (int slot=0; slot < INVENTORY_SIZE; slot++) {
        ItemStack part = inv.getStackInSlot(slot);
        if (part.isEmpty()) continue;
        out = IModularTool.addModule(out, part);
        inv.setStackInSlot(slot, ItemStack.EMPTY);
      }
    }
    return out;
  }

  protected void giveOrEjectAll (PlayerEntity player) {
    for (int slot=0; slot < INVENTORY_SIZE; slot++) {
      if (!giveSlot(player, slot)) {
        ejectAll();
        break;
      }
    }
  }

  protected void ejectAll () {
    for (int slot=0; slot < INVENTORY_SIZE; slot++) {
      ejectSlot(slot);
    }
  }

  protected boolean giveSlot (PlayerEntity player, int slot) {
    int invSlot = player.inventory.getFreeSlot();
    if (invSlot >= 0 && slot >= 0 && slot < INVENTORY_SIZE) {
      player.inventory.add(inv.getStackInSlot(slot));
      inv.setStackInSlot(slot, ItemStack.EMPTY);
      return true;
    }
    return false;
  }

  protected void ejectSlot (int slot) {
    if (level == null || inv.getStackInSlot(slot).isEmpty()) return;
    Vector3f pos = EJECT_POS.copy();
    pos.add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    level.addFreshEntity(new ItemEntity(this.level, pos.x(), pos.y(), pos.z(), inv.getStackInSlot(slot)));
    inv.setStackInSlot(slot, ItemStack.EMPTY);
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
  public void load(BlockState state, CompoundNBT cnbt) {
    super.load(state, cnbt);
    inv.deserializeNBT(cnbt.getCompound("inv"));
  }

  @Override
  public CompoundNBT save(CompoundNBT cnbt) {
    cnbt.put("inv", inv.serializeNBT());
    return super.save(cnbt);
  }
}
