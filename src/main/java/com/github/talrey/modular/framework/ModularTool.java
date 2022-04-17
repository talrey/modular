package com.github.talrey.modular.framework;

import com.github.talrey.modular.ModularToolsMod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

// the generic version, generally an error fallback but could be useful for extension
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;

public class ModularTool extends Item implements IModularTool {

  public ModularTool (Properties props) {
    super (props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return null;
  }

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
  public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int partialTicks) {
    ModularToolsMod.LOGGER.debug("EVENT: onUseTick");
  }

  @Override // when right-clicking a block
  public InteractionResult useOn(UseOnContext context) {
    ModularToolsMod.LOGGER.debug("EVENT: useOn");
    return InteractionResult.PASS;
  }

  @Override // happens every tick when mining a block: getHarvestLevel -> isCorrectToolForDrops -> this
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    ModularToolsMod.LOGGER.debug("EVENT: getDestroySpeed");
    return super.getDestroySpeed(stack, state);
  }

  @Override // happens if useOn defers or fails. Continuously triggers if getUseDuration is 0
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ModularToolsMod.LOGGER.debug("EVENT: use");
    return super.use(world, player, hand);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
    ModularToolsMod.LOGGER.debug("EVENT: finishUsingItem");
    return super.finishUsingItem(stack, world, entity);
  }

  @Override // happens when an attack swing connects
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
    ModularToolsMod.LOGGER.debug("EVENT: hurtEnemy");
    return false;
  }

  @Override
  public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
    ModularToolsMod.LOGGER.debug("EVENT: mineBlock");
    return super.mineBlock(stack, world, state, pos, entity);
  }

  @Override // happens every tick when mining a block, before getDestroySpeed
  public boolean isCorrectToolForDrops(BlockState state) {
    ModularToolsMod.LOGGER.debug("EVENT: isCorrectToolForDrops");
    return super.isCorrectToolForDrops(state);
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
    ModularToolsMod.LOGGER.debug("EVENT: interactLivingEntity");
    return super.interactLivingEntity(stack, player, target, hand);
  }

  @Override
  public void inventoryTick(ItemStack stack, Level world, Entity entity, int intarg, boolean boolarg) {
    super.inventoryTick(stack, world, entity, intarg, boolarg);
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: getUseAnimation");
    return super.getUseAnimation(stack);
  }

  @Override // happens every tick that right-click is held. Returns 0 by default
  public int getUseDuration(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: getUseDuration");
    return super.getUseDuration(stack);
  }

  @Override
  public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticksRemaining) {
    ModularToolsMod.LOGGER.debug("EVENT: releaseUsing");
    super.releaseUsing(stack, world, entity, ticksRemaining);
  }

  @Override // happens every tick on mouseover
  public Rarity getRarity(ItemStack stack) {
    //ModularToolsMod.LOGGER.debug("EVENT: getRarity");
    return super.getRarity(stack);
  }

  @Override
  public boolean useOnRelease(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: useOnRelease");
    return super.useOnRelease(stack);
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
