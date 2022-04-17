package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class ModularShield extends ShieldItem implements IModularTool {
  public ModularShield(Properties props) {
    super(props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() { return ItemRegistration.FUNCTION_SHIELD.get(); }

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

  @Override
  public boolean isBarVisible (ItemStack stack) {
    return IModularTool.isBarVisible(stack);
  }

  @Override
  public int getBarWidth (ItemStack stack) {
    return IModularTool.getBarWidth(stack);
  }

  @Override
  public int getBarColor (ItemStack stack) {
    return IModularTool.getBarColor(stack);
  }
}
