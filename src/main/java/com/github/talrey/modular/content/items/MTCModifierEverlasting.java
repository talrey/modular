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

  @Override
  public int getLayerBarColor (ItemStack stack) {
    return 0;
  }

  @Override
  public boolean isLayerBarVisible (ItemStack stack, Entity wielder) {
    return false; // we don't need to show it
  }

  @Override
  public int getLayerBarWidth (ItemStack stack, Entity wielder) {
    return 13; // full gauge, not that it'll be seen
  }

  @Override
  public boolean canConsume(ItemStack stack, int amount, Entity wielder) {
    return true;
  }

  @Override
  public int consume (ItemStack stack, int amount, Entity wielder) {
    return 0; // consume all incoming damage
  }

  @Override
  public int getMaxCapacity (ItemStack stack, Entity wielder) {
    return 1;
  }

  @Override
  public int getCurrentCapacity (ItemStack stack, Entity wielder) {
    return 1;
  }

  @Override
  public int recharge (ItemStack stack, int amount, Entity wielder) {
    return amount; // cannot accept any charge
  }
}
