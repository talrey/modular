package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public interface IModularTool {
  String NBT_TAG = "modules";
  String NBT_CORE      = "mt_core";
  String NBT_HANDLE    = "mt_handle";
  String NBT_FUNCTIONS = "mt_functions";
  String NBT_MODIFIERS = "mt_modifiers";

  ModularToolComponent getFunctionComponent ();

  static ActionResultType callAction (ActionContext ctx) {
    CompoundNBT modules = ctx.toolInUse.getOrCreateTag().getCompound(NBT_TAG);
    boolean result = false;
    if (modules.isEmpty()) return ActionResultType.PASS;
    result |= ItemRegistration.getMTC(modules.getInt(NBT_CORE)).call(ctx)
           || ItemRegistration.getMTC(modules.getInt(NBT_HANDLE)).call(ctx);
    int[] mods = modules.getIntArray(NBT_MODIFIERS);
    for (int slot=0; slot < mods.length; slot++) {
      result |= ItemRegistration.getMTC(mods[slot]).call(ctx);
    }
    int[] funcs = modules.getIntArray(NBT_FUNCTIONS);
    for (int slot=0; slot < funcs.length; slot++) {
      result |= ItemRegistration.getMTC(funcs[slot]).call(ctx);
    }
    if (result) return ActionResultType.SUCCESS;

    return ActionResultType.PASS; // none of the modules present reacted to this call
  }

  static ModularToolComponent[] getAllComponents (ItemStack tool) {
    ModularToolComponent[] modout = new ModularToolComponent[ToolAssemblerTE.INVENTORY_SIZE];
    if ( !(tool.getItem() instanceof IModularTool) || (tool.getTag() == null) || !(tool.getTag().contains(NBT_TAG)) ) return modout; // empty array

    CompoundNBT modules = tool.getTag().getCompound(NBT_TAG);
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
    if (!(in.getItem() instanceof IModularTool) || partIn.isEmpty() || !(partIn.getItem() instanceof ModularToolComponent module)) return in;

    CompoundNBT modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) in.getOrCreateTag().put(NBT_TAG, new CompoundNBT());

    // TODO handle durability of modules - intArray?
    int index = ItemRegistration.getIndexOfMTC(module);
    switch (module.partType) {
      // TODO what should happen if there's no slot available
      case CORE     -> modules.putInt(NBT_CORE, index);
      case HANDLE   -> modules.putInt(NBT_HANDLE, index);
      case FUNCTION -> {
        if (!modules.contains(NBT_FUNCTIONS)) modules.putIntArray(NBT_FUNCTIONS, new int[]{-1,-1,-1});
        int[] funcs = modules.getIntArray(NBT_FUNCTIONS);
        for (int slot=0; slot<funcs.length; slot++) {
          if (funcs[slot] < 0) {
            funcs[slot] = index;
            break;
          }
        }
      }
      case MODIFIER -> {
        if (!modules.contains(NBT_MODIFIERS)) modules.putIntArray(NBT_MODIFIERS, new int[] {-1,-1,-1});
        int[] mods = modules.getIntArray(NBT_MODIFIERS);
        for (int slot=0; slot<mods.length; slot++) {
          if (mods[slot] < 0) {
            mods[slot] = index;
            break;
          }
        }
      }
    }
    in.getTag().put(NBT_TAG, modules);
    return in;
  }

  static ItemStack removeModule (ItemStack in, ModularToolComponent module) {
    CompoundNBT modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) return in; // nothing to do here govnah

    int index = ItemRegistration.getIndexOfMTC(module);
    switch (module.partType) {
      case CORE     -> modules.remove(NBT_CORE);
      case HANDLE   -> modules.remove(NBT_HANDLE);
      case FUNCTION -> {
        if (!modules.contains(NBT_FUNCTIONS)) break;
        int[] slots = modules.getIntArray(NBT_FUNCTIONS);
        for (int slot=0; slot<slots.length; slot++) {
          if (slots[slot] == index) {
            slots[slot] = -1;
            break;
          }
        }
      }
      case MODIFIER -> {
        if (!modules.contains(NBT_MODIFIERS)) break;
        int[] slots = modules.getIntArray(NBT_MODIFIERS);
        for (int slot=0; slot<slots.length; slot++) {
          if (slots[slot] == index) {
            slots[slot] = -1;
            break;
          }
        }
      }
    }
    in.getTag().put(NBT_TAG, modules);
    return in;
  }

  static ItemStack cycleFunctions (ItemStack in) {
    if (! (in.getItem() instanceof IModularTool tool)) return in;

    CompoundNBT modules = in.getOrCreateTag().getCompound(NBT_TAG);
    if (modules.isEmpty()) return in;
 //   ModularToolsMod.LOGGER.debug("Found functions...");

    int index = ItemRegistration.getIndexOfMTC(tool.getFunctionComponent());
  //  ModularToolsMod.LOGGER.debug("Converting from tool type " + index + " = " + tool.getFunctionComponent().getPartName());
    int[] slots = modules.getIntArray(NBT_FUNCTIONS);
  //  ModularToolsMod.LOGGER.debug("Slots to check: " + slots.length);
    for (int slot=0; slot<slots.length; slot++) {
      if (slots[slot] == index) {
        if (slots[(slot + 1) % slots.length] <= 0) {
          index = slots[0];
        }
        else index = slots[slot + 1];
        break;
      }
    }
  //  ModularToolsMod.LOGGER.debug("Found tool type " + index + " = " + ItemRegistration.getMTC(index));
    ItemStack out = new ItemStack(((Item)ItemRegistration.getModularTool(ItemRegistration.getMTC(index))));
  //  ModularToolsMod.LOGGER.debug("Resulting in " + out.getItem().getClass().getName());
    out.setTag(in.getTag());
    return out;
  }

  static String getToolName (ItemStack tool) {
    CompoundNBT modules = tool.getOrCreateTag().getCompound(NBT_TAG);

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

  default ITextComponent getFormattedName (ItemStack stack) {
    return new StringTextComponent(getToolName(stack)).withStyle(stack.getRarity().color);
  }
}
