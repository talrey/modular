package com.github.talrey.modular.framework;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTE;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public interface IModularTool {
  String NBT_TAG = "modules";
  String NBT_CORE      = "mt_core";
  String NBT_HANDLE    = "mt_handle";
  String NBT_FUNCTIONS = "mt_functions";
  String NBT_MODIFIERS = "mt_modifiers";
  String NBT_DAMAGE    = "module_damage";

  ModularToolComponent getFunctionComponent ();

  static ModularToolComponent[] getAllComponents (ItemStack tool) {
    ModularToolComponent[] modout = new ModularToolComponent[ToolAssemblerTE.INVENTORY_SIZE];
    if ( !(tool.getItem() instanceof IModularTool) || (tool.getTag() == null) || !(tool.getTag().contains(NBT_TAG)) ) return modout; // empty array

    CompoundTag modules = tool.getTag().getCompound(NBT_TAG);
    int index = 2;
    modout[0] = ItemRegistration.getMTC(modules.getInt(NBT_CORE));
    modout[1] = ItemRegistration.getMTC(modules.getInt(NBT_HANDLE));
    int[] functions = modules.getIntArray(NBT_FUNCTIONS);
    for (int function : functions) {
      modout[index] = ItemRegistration.getMTC(function);
      index++;
    }
    int[] modifiers = modules.getIntArray(NBT_MODIFIERS);
    for (int modifier : modifiers) {
      modout[index] = ItemRegistration.getMTC(modifier);
      index++;
    }
    return modout;
  }

  static ItemStack addModule (ItemStack in, ItemStack partIn) {
    if (!(in.getItem() instanceof IModularTool) || partIn.isEmpty() || !(partIn.getItem() instanceof ModularToolComponent)) return in;
    ModularToolComponent module = (ModularToolComponent)partIn.getItem();

    CompoundTag modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) in.getOrCreateTag().put(NBT_TAG, new CompoundTag());
    int[] damage = new int[ToolAssemblerTE.INVENTORY_SIZE];
    if (modules.contains(NBT_DAMAGE)) damage = modules.getIntArray(NBT_DAMAGE);

    int index = ItemRegistration.getIndexOfMTC(module);
    switch (module.partType) {
      case CORE:     modules.putInt(NBT_CORE, index); damage[0] = partIn.getDamageValue(); break;
      case HANDLE:   modules.putInt(NBT_HANDLE, index); damage[1] = partIn.getDamageValue(); break;
      case FUNCTION:
        if (!modules.contains(NBT_FUNCTIONS)) modules.putIntArray(NBT_FUNCTIONS, new int[]{-1,-1,-1});
        int[] funcs = modules.getIntArray(NBT_FUNCTIONS);
        for (int slot=0; slot<funcs.length; slot++) {
          if (funcs[slot] < 0) {
            funcs[slot] = index;
            damage[2+slot] = partIn.getDamageValue();
            break;
          }
        }
        break;
      case MODIFIER:
        if (!modules.contains(NBT_MODIFIERS)) modules.putIntArray(NBT_MODIFIERS, new int[] {-1,-1,-1});
        int[] mods = modules.getIntArray(NBT_MODIFIERS);
        for (int slot=0; slot<mods.length; slot++) {
          if (mods[slot] < 0) {
            mods[slot] = index;
            damage[5+slot] = partIn.getDamageValue();
            break;
          }
        }
        break;
    }
    modules.putIntArray(NBT_DAMAGE, damage);
    in.getTag().put(NBT_TAG, modules);
    return module.onAssembly(in, partIn);
  }

  static ItemStack removeModule (ItemStack in, ModularToolComponent module) {
    CompoundTag modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) return in; // nothing to do here govnah

    int index = ItemRegistration.getIndexOfMTC(module);
    int[] slots;
    switch (module.partType) {
      case CORE:     modules.remove(NBT_CORE);
      case HANDLE:   modules.remove(NBT_HANDLE);
      case FUNCTION:
        if (!modules.contains(NBT_FUNCTIONS)) break;
        slots = modules.getIntArray(NBT_FUNCTIONS);
        for (int slot=0; slot<slots.length; slot++) {
          if (slots[slot] == index) {
            slots[slot] = -1;
            break;
          }
        }
        break;
      case MODIFIER:
        if (!modules.contains(NBT_MODIFIERS)) break;
        slots = modules.getIntArray(NBT_MODIFIERS);
        for (int slot=0; slot<slots.length; slot++) {
          if (slots[slot] == index) {
            slots[slot] = -1;
            break;
          }
        }
        break;
    }
    in.getTag().put(NBT_TAG, modules);
    return in;
  }

  static ItemStack cycleFunctions (ItemStack in) {
    if (! (in.getItem() instanceof IModularTool)) return in;
    IModularTool tool = (IModularTool)in.getItem();

    CompoundTag modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) return in;
 //   ModularToolsMod.LOGGER.debug("Found functions...");

    int index = ItemRegistration.getIndexOfMTC(tool.getFunctionComponent());
  //  ModularToolsMod.LOGGER.debug("Converting from tool type " + index + " = " + tool.getFunctionComponent().getPartName());
    int[] slots  = modules.getIntArray(NBT_FUNCTIONS);
    int[] damage = modules.getIntArray(NBT_DAMAGE);
    int oldDura  = in.getDamageValue();
    int newDura  = in.getDamageValue();
  //  ModularToolsMod.LOGGER.debug("Slots to check: " + slots.length);
    for (int slot=0; slot<slots.length; slot++) {
      if (slots[slot] == index) {
        if (slots[(slot + 1) % slots.length] <= 0) {
          index   = slots[0];
          newDura = damage[2];
        }
        else {
          index   = slots[slot + 1];
          newDura = damage[2 + slot + 1];
        }
        damage[2 + slot] = oldDura;
        break;
      }
    }
  //  ModularToolsMod.LOGGER.debug("Found tool type " + index + " = " + ItemRegistration.getMTC(index));
    ItemStack out = new ItemStack(((Item)ItemRegistration.getModularTool(ItemRegistration.getMTC(index))));
    out.setDamageValue(newDura);
  //  ModularToolsMod.LOGGER.debug("Resulting in " + out.getItem().getClass().getName());
    modules.putIntArray(NBT_DAMAGE, damage);
    out.getOrCreateTag().put(NBT_TAG, modules);
    return out;
  }

  static int tryAbsorbDamage (ItemStack stack, int amount, Entity wielder) {
    int remaining = amount;
    ModularToolComponent[] modules = IModularTool.getAllComponents(stack);
    for (ModularToolComponent mtc : modules) {
      if (mtc instanceof IDurabilityConverter && ((IDurabilityConverter)mtc).canConsume(stack, amount, wielder)) {
        remaining = ((IDurabilityConverter)mtc).consume(stack, amount, wielder);
      }
    }
    return remaining;
  }

  static String getToolName (ItemStack tool) {
    CompoundTag modules = tool.getOrCreateTag().getCompound(NBT_TAG);

    ModularToolComponent handle = ItemRegistration.getMTC(modules.getInt(NBT_HANDLE));
    ModularToolComponent core   = ItemRegistration.getMTC(modules.getInt(NBT_CORE));
    if (handle == null || core == null) return "how did you get this?";

    StringBuilder strb = new StringBuilder();
    strb.append(handle.partName);
    strb.append(' ');
    strb.append(core.partName);
    if (!core.partName.isEmpty()) strb.append(' ');
    int[] slots = modules.getIntArray(NBT_MODIFIERS);
    for (int slot=0; slot<slots.length; slot++) {
      if (slots[slot] < 0) continue;
      ModularToolComponent mod = ItemRegistration.getMTC(slots[slot]);
      if (mod == null) continue;
      strb.append(mod.partName);
      strb.append(' ');
    }
    slots = modules.getIntArray(NBT_FUNCTIONS);
    for (int slot=0; slot<slots.length; slot++) {
      if (slots[slot] < 0) continue;
      ModularToolComponent func = ItemRegistration.getMTC(slots[slot]);
      if (func == null) continue;
      strb.append(func.partName);
      strb.append(' ');
    }
    return strb.toString().trim();
  }

  default Component getFormattedName (ItemStack stack) {
    return new TextComponent(getToolName(stack)).withStyle(stack.getRarity().color);
  }
}
