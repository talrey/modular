package com.github.talrey.modular;

import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.world.item.ItemStack;

public class Util {

  public static ModularToolComponent castIfPresent (ItemStack entry) {
    if (entry != null && entry.getItem() instanceof ModularToolComponent) return (ModularToolComponent) entry.getItem();
    return null;
  }
}
