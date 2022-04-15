package com.github.talrey.modular.framework;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IDurabilityConverter {
  /**
   * Called when a Modular Tool would lose durability from an event.
   * @param stack the tool being damaged
   * @param amount the incoming damage
   * @param wielder who is holding the tool
   * @return whether the module can absorb the damage
   */
  public default boolean canConsume (ItemStack stack, int amount, Entity wielder) { return getCurrentCapacity(stack, wielder) > 0; }

  /**
   * Called after {@link #canConsume(int) canConsume} returns true, when the module blocks a damage event.
   * @param stack the tool being damaged
   * @param amount the incoming damage
   * @param wielder who, if anyone, is holding the tool
   * @return any remaining damage not absorbed by the module
   */
  public int consume (ItemStack stack, int amount, Entity wielder);

  /**
   * Reports how much "energy" the module can hold in total. This may not be equivalent to blockable damage if the module has an exchange rate.
   * @param stack the tool being queried
   * @param wielder who, if anyone, is holding the tool
   * @return the maximum capacity
   */
  public int getMaxCapacity (ItemStack stack, Entity wielder);

  /**
   * How much "energy" the module has remaining. This may not be equivalent to blockable damage if the module has an exchange rate.
   * @param stack the tool being queried
   * @param wielder who, if anyone, is holding the tool
   * @return the current capacity
   */
  public int getCurrentCapacity (ItemStack stack, Entity wielder);

  /**
   * Used to recharge a module's capacity. This may not be equivalent to blockable damage if the module has an exchange rate.
   * @param stack the tool being recharged
   * @param amount how much capacity to recover
   * @param wielder who, if anyone, is holding the tool
   * @return any leftover capacity the module couldn't accept
   */
  public int recharge (ItemStack stack, int amount, Entity wielder);
}
