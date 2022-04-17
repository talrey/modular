package com.github.talrey.modular.framework;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface IDurabilityConverter {
  /**
   * Determines the color of the durability bar while this modifier is active.
   * @param stack the tool being rendered
   * @return the color as an RGB hexadecimal encoded integer
   */
  public default int getLayerBarColor (ItemStack stack) {
    return Mth.hsvToRgb(Math.max(0.0F, 1.0F - (float) stack.getDamageValue() / stack.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
  }

  /**
   * Determine whether the over-bar should be shown.
   * @param stack the tool being rendered
   * @param wielder who is holding the tool (most likely the localPlayer)
   * @return true if the bar is visible
   */
  public boolean isLayerBarVisible (ItemStack stack, Entity wielder);

  /**
   * Determines the "fullness" of the durability gauge.
   * @param stack the tool being rendered
   * @param wielder who is holding the tool
   * @return the bar width
   */
  public int getLayerBarWidth (ItemStack stack, Entity wielder);

  /**
   * Called when a Modular Tool would lose durability from an event.
   * @param stack the tool being damaged
   * @param amount the incoming damage
   * @param wielder who is holding the tool
   * @return whether the module can absorb the damage
   */
  public default boolean canConsume (ItemStack stack, int amount, Entity wielder) { return getCurrentCapacity(stack, wielder) > 0; }

  /**
   * Called after {@link #canConsume(ItemStack, int, Entity) canConsume} returns true, when the module blocks a damage event.
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

  /**
   * Determines whether a module can be recharged.
   * @param stack the tool to recharge
   * @param wielder who, if anyone, is holding the tool
   * @return true if the tool can accept charge
   */
  public default boolean canRecharge (ItemStack stack, Entity wielder) {
    return (getCurrentCapacity(stack, wielder) < getMaxCapacity(stack, wielder));
  }
}
