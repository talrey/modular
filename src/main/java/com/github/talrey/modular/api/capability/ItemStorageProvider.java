package com.github.talrey.modular.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStorageProvider implements ICapabilitySerializable<CompoundTag> {

  public static Capability<ItemStackHandler> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
  private ItemStackHandler stackHandler = null;
  private final LazyOptional<ItemStackHandler> opt = LazyOptional.of(this::createStackHandler);

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == CAPABILITY) return opt.cast();

    return LazyOptional.empty();
  }

  @NotNull
  private ItemStackHandler createStackHandler () {
    if (stackHandler == null) {
      stackHandler = new ItemStackHandler(9);
    }
    return stackHandler;
  }

  @Override
  public CompoundTag serializeNBT() {
    try {
      return opt.orElseThrow(CapabilityNotFoundException::new).serializeNBT();
    } catch (CapabilityNotFoundException e) {
      return new CompoundTag();
    }
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    try {
      opt.orElseThrow(CapabilityNotFoundException::new).deserializeNBT(nbt);
    } catch (CapabilityNotFoundException ignored) {
    }
  }
}
