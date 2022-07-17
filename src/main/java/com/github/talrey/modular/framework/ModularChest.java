package com.github.talrey.modular.framework;

import com.github.talrey.modular.api.capability.CapabilityNotFoundException;
import com.github.talrey.modular.api.capability.ItemStorageProvider;
import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModularChest extends Item implements IModularTool, MenuProvider {

  public ModularChest (Item.Properties props) {
    super(props);
  }

//  @Nullable
//  @Override
//  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//    return new ItemStorageProvider();
//  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return ItemRegistration.FUNCTION_CHEST.get();
  }

  @NotNull
  @Override
  public Component getName (ItemStack tool) {
    if (tool.getTag() != null && tool.getTag().contains(NBT_TAG)) return getFormattedName(tool);
    /*else*/ return super.getName(tool);
  }

  @Override
  public void fillItemCategory (CreativeModeTab group, NonNullList<ItemStack> list) {
    //fillItemCategory(group, list);
  }

  @Override
  public InteractionResultHolder<ItemStack> use (Level world, Player user, InteractionHand hand) {
    if (user.isShiftKeyDown()) {
      return InteractionResultHolder.success(IModularTool.cycleFunctions(user.getItemInHand(hand)));
    }
    user.openMenu(this);
    return super.use(world, user, hand);
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

  // MenuProvider //
  @Override
  public Component getDisplayName() {
    return new TextComponent("Tool Storage");
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    ItemStack tool;
    if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(this)) {
      tool = player.getItemInHand(InteractionHand.MAIN_HAND);
    }
    else tool = player.getItemInHand(InteractionHand.OFF_HAND);
    return new ChestMenu(MenuType.GENERIC_9x1, id, inv, new StackStoredContainer(tool, 9), 1);
  }
  //
}
