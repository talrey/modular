package com.github.talrey.modular.framework.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModularToolCapability implements ICapabilitySerializable<CompoundTag> {
  public static final IModularToolSystem DEFAULT = new ModularToolSystem();
  public static final Capability<IModularToolSystem> MTS = CapabilityManager.get(new CapabilityToken<IModularToolSystem>() {});

  private final Capability<IModularToolSystem> cap;
  private final LazyOptional<IModularToolSystem> impl;

  public ModularToolCapability () {
    this (MTS, LazyOptional.of(()-> DEFAULT));
  }

  public ModularToolCapability (Capability<IModularToolSystem> capability, LazyOptional<IModularToolSystem> lazyoptional) {
    cap = capability;
    impl = lazyoptional;
  }

  public static ModularToolCapability from (Capability<IModularToolSystem> capability, NonNullSupplier<IModularToolSystem> impl) {
    return new ModularToolCapability(capability, LazyOptional.of(impl));
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> capability, @Nullable Direction side) {
    if (capability == cap) return impl.cast();
    return LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT () {
    return impl.orElseGet(()-> DEFAULT).serializeNBT();
  }

  @Override
  public void deserializeNBT (CompoundTag nbt) {
    impl.orElseGet(()-> DEFAULT).deserializeNBT(nbt);
  }
}
