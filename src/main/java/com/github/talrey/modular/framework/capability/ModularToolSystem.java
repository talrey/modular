package com.github.talrey.modular.framework.capability;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.Util;
import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.ModularToolComponent;
import com.github.talrey.modular.framework.network.ModularToolsPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.function.BiFunction;

public class ModularToolSystem implements IModularToolSystem {
  public static final ResourceLocation CAPID = new ResourceLocation (ModularToolsMod.MODID, "tool");

  public static final String NBT_CORE   = "module_core";
  public static final String NBT_HANDLE = "module_handle";
  public static final String NBT_FUNCS  = "module_functions";
  public static final String NBT_MODS   = "module_modifiers";

  private ItemStack core, handle;
  private ItemStack[] functions, modifiers;
  private int activeType;

  public ModularToolSystem () {
    functions = new ItemStack[3];
    modifiers = new ItemStack[3];

    core   = ItemStack.EMPTY;
    handle = ItemStack.EMPTY;
    Arrays.fill(functions, ItemStack.EMPTY);
    Arrays.fill(modifiers, ItemStack.EMPTY);
    activeType = 0;
  }

  @Override
  public ModularToolComponent[] getComponents () {
    ModularToolComponent[] ret = new ModularToolComponent[2 + functions.length + modifiers.length];
    ret[0] = Util.castIfPresent(core);
    ret[1] = Util.castIfPresent(handle);
    for (int index = 0; index < functions.length; index++) ret[index + 2] = Util.castIfPresent(functions[index]);
    for (int index = 0; index < modifiers.length; index++) ret[index + 2 + functions.length] = Util.castIfPresent(modifiers[index]);
    return ret;
  }

  @Override
  public void copy (IModularToolSystem from) {
    for (ModularToolComponent mts : from.getComponents()) attachComponent(findComponent(mts));
  }

  @Override
  public ItemStack findComponent (ModularToolComponent type) {
    if (type == null) return ItemStack.EMPTY;
    return switch (type.getType()) {
      case CORE     -> core;
      case HANDLE   -> handle;
      case FUNCTION -> foreach(functions, (i,s)-> s.getItem().equals(type));
      case MODIFIER -> foreach(modifiers, (i,s)-> s.getItem().equals(type));
    };
  }

  @Override
  public ItemStack[] findComponents (ComponentType type) {
    ItemStack[] ret;
    switch (type) {
      case CORE     -> ret = new ItemStack[] { core };
      case HANDLE   -> ret = new ItemStack[] { handle };
      case FUNCTION -> ret = functions;
      case MODIFIER -> ret = modifiers;
      default       -> ret = new ItemStack[] { ItemStack.EMPTY };
    }
    return ret;
  }

  @Override
  public void attachComponent (ItemStack component) {
    if (component.getItem() instanceof ModularToolComponent mtc) {
      switch (mtc.getType()) {
        case CORE -> {
          if (core.isEmpty()) core = component;
        }
        case HANDLE -> {
          if (handle.isEmpty()) handle = component;
        }
        case FUNCTION -> {
          for (int index = 0; index < functions.length; index++) {
            if (functions[index].getItem().equals(component.getItem())) break;
            if (functions[index].isEmpty()) {
              functions[index] = component;
              break;
            }
          }
        }
        case MODIFIER -> {
          for (int index = 0; index < modifiers.length; index++) {
            if (modifiers[index].getItem().equals(component.getItem())) break;
            if (modifiers[index].isEmpty()) {
              modifiers[index] = component;
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public ItemStack removeComponent (ItemStack component) {
    ItemStack ret = ItemStack.EMPTY;
    if (component.getItem() instanceof ModularToolComponent mtc) {
      ret = removeComponent(mtc);
    }
    return ret;
  }

  @Override
  public ItemStack removeComponent (ModularToolComponent mtc) {
    ItemStack ret = ItemStack.EMPTY;
    switch (mtc.getType()) {
      case CORE -> {
        ret = core.copy();
        core = ItemStack.EMPTY;
      }
      case HANDLE -> {
        ret = handle.copy();
        handle = ItemStack.EMPTY;
      }
      case FUNCTION -> {
        for (int index = 0; index < functions.length; index++) if (functions[index].getItem().equals(mtc)) {
          ret = functions[index].copy();
          functions[index] = ItemStack.EMPTY;
        }
      }
      case MODIFIER -> {
        for (int index = 0; index < modifiers.length; index++) if (modifiers[index].getItem().equals(mtc)) {
          ret = modifiers[index].copy();
          modifiers[index] = ItemStack.EMPTY;
        }
      }
    }
    return ret;
  }

  @Override
  public ModularToolComponent getActiveFunction () {
    return functions[activeType].isEmpty() ? null : (ModularToolComponent) functions[activeType].getItem();
  }

  @Override
  public ItemStack cycleFunction (Player user, ItemStack oldTool) {
    if (user.level.isClientSide) return oldTool; // handle update with packet
    do {
      activeType = (activeType + 1) % functions.length;
    } while (functions[activeType].isEmpty());
    int slot = user.getInventory().findSlotMatchingItem(oldTool);
    ItemStack newStack = transform(user, oldTool, (ModularToolComponent) functions[activeType].getItem());
    ModularToolsPacketHandler.updateToolClientSide(newStack.serializeNBT(), (ServerPlayer)user, slot);
    return newStack;
  }

  @Override
  public ItemStack transform (Player user, ItemStack oldTool, ModularToolComponent newFunction) {
    if (user.level.isClientSide) return oldTool;
    for (int i = 0; i < functions.length; i++) {
      if (functions[i].getItem() == newFunction) {
        activeType = i;
        break;
      }
    }
    int slot = user.getInventory().findSlotMatchingItem(oldTool);
    ItemStack newStack = IModularToolSystem.super.transform(user, oldTool, newFunction);
    ModularToolsPacketHandler.updateToolClientSide(newStack.serializeNBT(), (ServerPlayer)user, slot);
    return newStack;
  }

  @Override
  public CompoundTag serializeNBT () {
    CompoundTag cnbt = new CompoundTag();
    if (!core.isEmpty()) cnbt.put(NBT_CORE, core.serializeNBT());
    if (!core.isEmpty()) cnbt.put(NBT_HANDLE, core.serializeNBT());
    CompoundTag funcs = new CompoundTag();
    for (int index = 0; index < functions.length; index++) {
      if (!functions[index].isEmpty()) funcs.put("f" + index, functions[index].serializeNBT());
    }
    cnbt.put(NBT_FUNCS, funcs);

    CompoundTag mods = new CompoundTag();
    for (int index = 0; index < modifiers.length; index++) {
      if (!modifiers[index].isEmpty()) mods.put("m" + index, modifiers[index].serializeNBT());
    }
    cnbt.put(NBT_MODS, mods);
    return cnbt;
  }

  @Override
  public void deserializeNBT (CompoundTag cnbt) {
    if (cnbt.contains(NBT_CORE))     core.deserializeNBT( (CompoundTag)cnbt.get(NBT_CORE));
    if (cnbt.contains(NBT_HANDLE)) handle.deserializeNBT( (CompoundTag)cnbt.get(NBT_HANDLE));
    if (cnbt.contains(NBT_FUNCS)) {
      CompoundTag funcNBT = (CompoundTag)cnbt.get(NBT_FUNCS);
      for (int index = 0; index < functions.length; index++) {
        if (!funcNBT.contains("f" + index)) continue;
        functions[index].deserializeNBT( (CompoundTag)funcNBT.get("f" + index));
      }
    }
    if (cnbt.contains(NBT_MODS)) {
      CompoundTag modNBT = (CompoundTag)cnbt.get(NBT_MODS);
      for (int index = 0; index < functions.length; index++) {
        if (!modNBT.contains("m" + index)) continue;
        modifiers[index].deserializeNBT( (CompoundTag)modNBT.get("m" + index));
      }
    }
  }

  private static ItemStack foreach (ItemStack[] array, BiFunction<Integer, ItemStack, Boolean> operator) {
    for (int index = 0; index < array.length; index++) {
      if (operator.apply(index, array[index])) return array[index];
    }
    return ItemStack.EMPTY;
  }
}
