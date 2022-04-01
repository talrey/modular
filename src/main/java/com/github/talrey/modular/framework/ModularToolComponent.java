package com.github.talrey.modular.framework;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ModularToolComponent extends Item {
  protected String partName;
  protected String looseName;
  protected ComponentType partType;

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

  @Override
  public ITextComponent getName(ItemStack stack) {
    return new StringTextComponent(getItemName()).withStyle(super.getName(stack).getStyle());
  }

  public ItemStack onAssembly (ItemStack tool, ItemStack component) {
    return tool;
  }

  public ItemStack onRemoval (ItemStack tool) {
    return new ItemStack(this);
  }
}
