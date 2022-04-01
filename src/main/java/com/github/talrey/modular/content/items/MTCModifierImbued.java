package com.github.talrey.modular.content.items;

import com.github.talrey.modular.Config;
import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class MTCModifierImbued extends ModularToolComponent {
  public MTCModifierImbued (String name, Properties props) {
    super(name, "Imbued", ComponentType.MODIFIER, props);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return !stack.isEnchanted();
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return !stack.isEnchanted(); // BookItem checks stack count, but MTCs are unstackable
  }

  @Override
  public int getEnchantmentValue() {
    return Config.getIntSetting(Config.IMBUED_TIER);
  }

  @Override
  public ItemStack onAssembly(ItemStack tool, ItemStack component) {
    Map<Enchantment, Integer> partmagic = EnchantmentHelper.getEnchantments(component);
    partmagic.forEach(tool::enchant);
    return tool;
  }

  @Override
  public ItemStack onRemoval (ItemStack tool) {
    ItemStack part = new ItemStack(this);
    Map<Enchantment, Integer> toolmagic = EnchantmentHelper.getEnchantments(tool);
    toolmagic.forEach(part::enchant);
    return part;
  }
}
