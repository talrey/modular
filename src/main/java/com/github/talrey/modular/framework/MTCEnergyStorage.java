package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.items.MTCModifierCharged;
import com.github.talrey.modular.framework.capability.CapabilityNotPresentException;
import com.github.talrey.modular.framework.capability.ModularToolCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MTCEnergyStorage implements IEnergyStorage, ICapabilityProvider {
  private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(()-> this);
  private final ItemStack stack;

  public MTCEnergyStorage (ItemStack stack) {
    this.stack = stack;
  }

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    return CapabilityEnergy.ENERGY.orEmpty(cap, holder);
  }

  private MTCModifierCharged getConverter () {
    MTCModifierCharged mtc = null;
    if (stack.getItem() instanceof MTCModifierCharged) {
      mtc = (MTCModifierCharged)stack.getItem();
    }
    else if (stack.getItem() instanceof IModularTool) {
      ItemStack[] parts = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new).findComponents(ComponentType.MODIFIER);
      for (ItemStack part : parts) {
        if (part.getItem() instanceof MTCModifierCharged) {
          mtc = (MTCModifierCharged) part.getItem();
          break;
        }
      }
    }
    return mtc;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int result = 0;
    if (canReceive()) {
      int stored = this.getEnergyStored();
      result = Math.min(getMaxEnergyStored() - stored, maxReceive);
      if (!simulate) {
        MTCModifierCharged mtc = getConverter();
        if (mtc != null) mtc.recharge(stack, result, null);
      }
    }
    return result;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    int result = 0;
    if (canExtract()) {
      int stored = this.getEnergyStored();
      result = Math.min(stored, maxExtract);
      if (!simulate) {
        MTCModifierCharged mtc = getConverter();
        if (mtc != null) mtc.consume(stack, result, null);
      }
    }
    return result;
  }

  @Override
  public int getEnergyStored() {
    MTCModifierCharged mtc = getConverter();
    if (mtc != null) {
      return mtc.getCurrentCapacity(stack, null);
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored() {
    MTCModifierCharged mtc = getConverter();
    if (mtc != null) {
      return mtc.getMaxCapacity(stack, null);
    }
    return 0;
  }

  @Override
  public boolean canExtract() {
    MTCModifierCharged mtc = getConverter();
    if (mtc != null) {
      return mtc.canConsume(stack, 1, null);
    }
    return false;
  }

  @Override
  public boolean canReceive() {
    MTCModifierCharged mtc = getConverter();
    if (mtc != null) {
      return mtc.canRecharge(stack, null);
    }
    return false;
  }
}
