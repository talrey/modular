package com.github.talrey.modular.framework;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.function.Function;

public class ModularToolComponent extends Item {
  protected String partName;
  protected String looseName;
  protected ComponentType partType;
  private HashMap<ActionType, Function<ActionContext, Boolean>> actions = new HashMap<>();

  public ModularToolComponent(String name, ComponentType type, Properties props) {
    this(name, name, type, props);
  }

  public ModularToolComponent(String name, String assembledName, ComponentType type, Properties props) {
    super(props);
    this.looseName = name;
    this.partName  = assembledName;
    this.partType = type;
  }

  public boolean is (ComponentType ct) {
    return partType == ct;
  }

  public String getItemName ()    { return looseName; }
  public String getPartName ()    { return partName;  }
  public ComponentType getType () { return partType;  }

  public ModularToolComponent subscribe (ActionType type, Function<ActionContext, Boolean> action) {
    actions.put(type, action);
    return this;
  }

  protected boolean call (ActionContext ctx) {
    return actions.containsKey(ctx.type) && actions.get(ctx.type).apply(ctx);
  }

  @Override
  public ITextComponent getName(ItemStack stack) {
    return new StringTextComponent(getItemName()).withStyle(super.getName(stack).getStyle());
  }
}
