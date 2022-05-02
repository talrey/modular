package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class ModularBow extends BowItem implements IModularTool {
  public ModularBow(Properties props) {
    super(props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return ItemRegistration.FUNCTION_BOW.get();
  }

  @Override
  public Component getName (ItemStack tool) {
    if (tool.getTag() != null && tool.getTag().contains("display")) return super.getName(tool);;
    /*else*/ return getFormattedName(tool);
  }

  @Override
  public void fillItemCategory (CreativeModeTab group, NonNullList<ItemStack> list) {
    //fillItemCategory(group, list);
  }

  @Override
  public InteractionResultHolder<ItemStack> use (Level world, Player user, InteractionHand hand) {
    if (user.isShiftKeyDown()) {
      return cycle(user, user.getItemInHand(hand));
    }
    return super.use(world, user, hand);
  }

  @Override
  public <T extends LivingEntity> int damageItem (ItemStack stack, int amount, T entity, java.util.function.Consumer<T> onBroken) {
    IModularTool.absorbDamage(stack, amount, entity);
    return 0;
  }

  @Override
  public boolean isDamageable (ItemStack stack) {
    return false;
  }

  @Override
  public boolean isEnchantable (ItemStack tool) {
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

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundTag nbt) {
    return IModularTool.onInitCapabilities(stack, nbt);
  }

  @Nullable
  @Override
  public CompoundTag getShareTag (ItemStack stack) {
    return IModularTool.getShareTag(stack);
  }

  @Override
  public void readShareTag (ItemStack stack, @Nullable CompoundTag nbt) {
    super.readShareTag(stack, nbt);
    IModularTool.readShareTag(stack, nbt);
  }
}
