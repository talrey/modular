package com.github.talrey.modular.content.blocks.assembler;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import com.github.talrey.modular.framework.capability.CapabilityNotPresentException;
import com.github.talrey.modular.framework.capability.ModularToolCapability;
import com.github.talrey.modular.framework.network.ModularToolsPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.mojang.math.Vector3f;
import net.minecraftforge.items.ItemStackHandler;

public class ToolAssemblerTE extends BlockEntity {

  public static final int INVENTORY_SIZE = 8;

  private final ItemStackHandler inv;
  private static final Vector3f EJECT_POS = new Vector3f(0.5f, 1f, 0.5f);

  public ToolAssemblerTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
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
        out.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new).attachComponent(part);
        inv.setStackInSlot(slot, ItemStack.EMPTY);
      //  ModularToolsMod.LOGGER.debug("Added component [" + ((ModularToolComponent)part.getItem()).getItemName() + "] on side " + (level.isClientSide ? "client" : "server"));
      }
    }
    return out;
  }

  protected void giveOrEjectAll (Player player) {
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

  protected boolean giveSlot (Player player, int slot) {
    int invSlot = player.getInventory().getFreeSlot();
    if (invSlot >= 0 && slot >= 0 && slot < INVENTORY_SIZE) {
      player.getInventory().add(inv.getStackInSlot(slot));
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
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  protected ItemStackHandler getInventory () {
    return this.inv;
  }

  @Override
  public void load (CompoundTag cnbt) {
    super.load(cnbt);
    inv.deserializeNBT(cnbt.getCompound("inv"));
  }

  @Override
  public void saveAdditional (CompoundTag cnbt) {
    cnbt.put("inv", inv.serializeNBT());
    super.saveAdditional (cnbt);
  }
}
