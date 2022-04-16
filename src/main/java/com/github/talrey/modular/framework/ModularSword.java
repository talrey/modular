package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class ModularSword extends SwordItem implements IModularTool {

  // default makes it pretty weak
  public ModularSword (Properties props) {
    this(Tiers.WOOD, 1, 1f, props);
  }

  public ModularSword (Tier itemTier, int baseDamage, float baseAttackSpeed, Properties props) {
    super(itemTier, baseDamage, baseAttackSpeed, props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() { return ItemRegistration.FUNCTION_BLADE.get(); }

  @Override
  public Component getName(ItemStack tool) {
    if (tool.getTag() != null && tool.getTag().contains(NBT_TAG)) return getFormattedName(tool);
    /*else*/ return super.getName(tool);
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
    //fillItemCategory(group, list);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
    if (user.isShiftKeyDown()) {
      return InteractionResultHolder.success(IModularTool.cycleFunctions(user.getItemInHand(hand)));
    }
    return super.use(world, user, hand);
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, java.util.function.Consumer<T> onBroken) {
    int remaining = IModularTool.tryAbsorbDamage(stack, amount, entity);
    return super.damageItem(stack, remaining, entity, onBroken);
  }

  @Override
  public boolean isEnchantable(ItemStack tool) {
    return false;
  }
}
