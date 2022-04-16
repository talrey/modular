package com.github.talrey.modular.content.items;

import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.IDurabilityConverter;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class MTCModifierEverlasting extends ModularToolComponent implements IDurabilityConverter {
  public MTCModifierEverlasting(String name, Properties props) {
    super(name, "Everlasting", ComponentType.MODIFIER, props);
  }

  @java.lang.Override
  public boolean canConsume(ItemStack stack, int amount, Entity wielder) {
    return true;
  }

  @java.lang.Override
  public int consume (ItemStack stack, int amount, Entity wielder) {
    return 0; // consume all incoming damage
  }

  @java.lang.Override
  public int getMaxCapacity (ItemStack stack, Entity wielder) {
    return 1;
  }

  @java.lang.Override
  public int getCurrentCapacity (ItemStack stack, Entity wielder) {
    return 1;
  }

  @java.lang.Override
  public int recharge (ItemStack stack, int amount, Entity wielder) {
    return amount; // cannot accept any charge
  }
}
