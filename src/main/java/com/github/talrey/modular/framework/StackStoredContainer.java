package com.github.talrey.modular.framework;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StackStoredContainer extends SimpleContainer {
  private static final String TAG = "storage";
  private final ItemStack holder;

  public StackStoredContainer (ItemStack stack, int expectedSize) {
    super(expectedSize);
    holder = stack;
    ListTag list;
    if (!stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(TAG)) {
      list = stack.getTag().getList(TAG, Tag.TAG_COMPOUND);
      for (int slot = 0; slot < expectedSize && slot < list.size(); slot++) {
        setItem(slot, ItemStack.of(list.getCompound(slot)));
      }
    }
  }

  @Override
  public void setChanged() {
    super.setChanged();
    ListTag list = new ListTag();
    for (int slot = 0; slot < getContainerSize(); slot++) {
      list.add(getItem(slot).save(new CompoundTag()));
    }
    holder.getOrCreateTag().put(TAG, list);
  }

  @Override
  public boolean stillValid(Player player) {
    return !holder.isEmpty();
  }
}
