package com.github.talrey.modular.framework;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

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
  public Component getName(ItemStack stack) {
    return new TextComponent(getItemName()).withStyle(super.getName(stack).getStyle());
  }

  public ItemStack onAssembly (ItemStack tool, ItemStack component) {
    return tool;
  }

  public ItemStack onRemoval (ItemStack tool) {
    return new ItemStack(this);
  }

  public boolean extraActionOnUse (ItemStack tool, Level world, LivingEntity user, InteractionHand hand) { return false; }

  public boolean extraActionOnHold (ItemStack tool, Level world, LivingEntity user, InteractionHand hand) { return false; }

  public boolean extraActionOnEndUse (ItemStack tool, Level world, LivingEntity user, InteractionHand hand) { return false; }
}
