package com.github.talrey.modular.content.items;

import com.github.talrey.modular.Config;
import com.github.talrey.modular.framework.ActionType;
import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.ModularToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

public class MTCModifierImbued extends ModularToolComponent {
  public MTCModifierImbued (String name, Properties props) {
    super(name, "Imbued", ComponentType.MODIFIER, props);
    //subscribe(ActionType.ASSEMBLE, ctx -> {
    //  ctx.toolInUse.enchant(Enchantments.VANISHING_CURSE, 1);
    //  return true;
    //});
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
}
