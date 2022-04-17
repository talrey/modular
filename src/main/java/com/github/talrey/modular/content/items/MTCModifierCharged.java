package com.github.talrey.modular.content.items;

import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.IDurabilityConverter;
import com.github.talrey.modular.framework.MTCEnergyStorage;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.Nullable;

public class MTCModifierCharged extends ModularToolComponent implements IDurabilityConverter {
  private static final String NBT_ENERGY = "Energy";
  private static final int MAX_ENERGY = 1000;
  private static final int USE_COST   =    1;
  private static final int BAR_COLOR  = 0x5F5FFF;

  public MTCModifierCharged(String name, Properties props) {
    super(name, "Charged", ComponentType.MODIFIER, props);
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new MTCEnergyStorage(stack);
  }

  @Override
  public ItemStack onAssembly(ItemStack tool, ItemStack component) {
    component.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage-> {
      tool.getOrCreateTag().putInt(NBT_ENERGY, storage.getEnergyStored());
    });
    return super.onAssembly(tool, component);
  }

  @Override
  public ItemStack onRemoval(ItemStack tool) {
    ItemStack ret = super.onRemoval(tool);
    ret.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage-> {
      if (!tool.getOrCreateTag().contains(NBT_ENERGY)) return;
      storage.receiveEnergy(tool.getOrCreateTag().getInt(NBT_ENERGY), false);
    });
    return ret;
  }

  // == IDurabilityProvider == \\
  @Override
  public int getLayerBarColor (ItemStack stack) {
    return BAR_COLOR;
  }

  @Override
  public boolean isLayerBarVisible (ItemStack stack, Entity wielder) {
    return getCurrentCapacity(stack, wielder) > 0;
  }

  @Override
  public int getLayerBarWidth (ItemStack stack, Entity wielder) {
    return Math.round(13f * Mth.clamp(getCurrentCapacity(stack, wielder)*1f / getMaxCapacity(stack, wielder), 0, 1));
  }

  @Override
  public int consume(ItemStack stack, int amount, Entity wielder) {
    int charge = getCurrentCapacity(stack, wielder) - (amount * USE_COST);
    if (charge < 0) {
      amount = -(charge / USE_COST);
      charge = 0;
    }
    else amount = 0;

    stack.getTag().putInt(NBT_ENERGY, charge);
    return amount;
  }

  @Override
  public int getMaxCapacity(ItemStack stack, Entity wielder) {
    return MAX_ENERGY;
  }

  @Override
  public int getCurrentCapacity(ItemStack stack, Entity wielder) {
    if (!stack.getOrCreateTag().contains(NBT_ENERGY)) stack.getTag().putInt(NBT_ENERGY, 0);

    return stack.getTag().getInt(NBT_ENERGY);
  }

  @Override
  public int recharge (ItemStack stack, int amount, Entity wielder) {
    int current = getCurrentCapacity(stack, wielder);
    int max     = getMaxCapacity(stack, wielder);
    current += amount;
    if (current > max) {
      amount = current - max;
      current = max;
    }
    else amount = 0;

    stack.getTag().putInt(NBT_ENERGY, current);
    return amount;
  }
}
