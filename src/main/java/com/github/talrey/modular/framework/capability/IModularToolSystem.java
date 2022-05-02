package com.github.talrey.modular.framework.capability;

import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public interface IModularToolSystem extends INBTSerializable<CompoundTag> {
  /**
   * @return Array of ModularToolComponents representing all parts of this tool
   */
  ModularToolComponent[] getComponents ();

  /**
   * Fills the Modular Tool with another one's components
   * @param from the Modular Tool to read from
   */
  void copy (IModularToolSystem from);

  /**
   * Assigns a module to a tool. Does nothing if the tool cannot accept more of that module
   * @param component the ModularToolComponent item stack to attach
   */
  void attachComponent (ItemStack component);

  /**
   * Clears a specified module from a tool.
   * @param component the component to find and remove
   * @return the component that was found
   */
  ItemStack removeComponent (ItemStack component);

  /**
   * Alternate method of removing a module from a tool, using the type to search
   * @param mtc the component type to find and remove
   * @return the ItemStack of the found component
   */
  ItemStack removeComponent (ModularToolComponent mtc);

  /**
   * Look for a module on a tool
   * @param component
   * @return
   */
  ItemStack findComponent (ModularToolComponent component);

  /**
   * Gets the list of all components of a specified type (see ComponentType enum)
   * @param type the ComponentType to get
   * @return the components on this tool of that type
   */
  ItemStack[] findComponents (ComponentType type);

  /**
   * Consume durability on the active function
   * @param amount how much durability to use
   */
  default void damageTool (int amount) {
    ItemStack active = findComponent(getActiveFunction());
    if (!active.isEmpty()) active.setDamageValue( active.getDamageValue() + amount );
  }

  default int getDamage () {
    return findComponent(getActiveFunction()).getDamageValue();
  }

  default int getMaxDamage () {
    return findComponent(getActiveFunction()).getMaxDamage();
  }

  /**
   * @return true if the active function has run out of durability
   */
  default boolean isBroken () {
    ItemStack active = findComponent(getActiveFunction());
    return (active.isEmpty() || (active.getDamageValue() >= active.getMaxDamage()));
  }

  /**
   * @return the class of ModularToolComponent that represents the currently active tool function
   */
  ModularToolComponent getActiveFunction ();

  /**
   * Switch to the "next" functional module on this tool.
   *
   * @param user the player triggering the cycle
   * @param oldTool the tool just prior to switching
   * @return the tool after switching
   */
  ItemStack cycleFunction (Player user, ItemStack oldTool);

  /**
   * Framework method for swapping a modular tool's active function arbitrarily.
   * Note that this can change the tool to a function it might not even have.
   * @param user the player triggering the change
   * @param oldTool the tool just prior to switching
   * @param newFunction the new active function to perform
   * @return the tool after switching
   */
  default ItemStack transform (Player user, ItemStack oldTool, ModularToolComponent newFunction) {
    IModularTool tool = ItemRegistration.getModularTool(newFunction);
    ItemStack result;
    if (tool != ItemRegistration.TOOL_GENERIC.get()) {
      result = new ItemStack( (Item)tool );
      result.setTag(oldTool.getTag());
      oldTool.getCapability(ModularToolCapability.MTS).ifPresent(inmts -> {
        result.setDamageValue(inmts.findComponent(newFunction).getDamageValue());
        result.getCapability(ModularToolCapability.MTS).ifPresent(outmts -> outmts.copy(inmts));
      });
    }
    else result = ItemStack.EMPTY;

    return result;
  }
}
