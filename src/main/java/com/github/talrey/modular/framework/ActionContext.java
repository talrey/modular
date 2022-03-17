package com.github.talrey.modular.framework;

import net.minecraft.item.ItemStack;

public class ActionContext {
  protected ActionType type;

  public ItemStack toolInUse;
  public Object data;

  public ActionContext (ItemStack tool, ActionType type, Object optionalData) {
    this.toolInUse = tool;
    this.type = type;
    this.data = optionalData;
  }

  public boolean is (ActionType type) { return type == this.type; }

}
