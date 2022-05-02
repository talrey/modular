package com.github.talrey.modular.framework;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ModularToolComponent extends Item {
  protected final String partName;
  protected final String looseName;
  protected final ComponentType partType;

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

  public boolean equals (ModularToolComponent other) {
    return this.partType == other.partType
      && this.looseName.equals(other.looseName)
      && this.partName.equals(other.partName);
  }

  public String getItemName ()    { return looseName; }
  public String getPartName ()    { return partName;  }
  public ComponentType getType () { return partType;  }

  @Override
  public Component getName(ItemStack stack) {
    return new TextComponent(getItemName()).withStyle(super.getName(stack).getStyle());
  }

  public ItemStack onAssembly (ItemStack tool, ItemStack component) {
    return tool;
  }

  public ItemStack onRemoval (ItemStack tool) {
    return new ItemStack(this);
  }
}
