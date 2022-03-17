package com.github.talrey.modular.framework;

import com.github.talrey.modular.ModularToolsMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Set;

public class ModularTool extends Item implements IModularTool {

  public ModularTool (Properties props) {
    super (props);
  }

  @Override
  public ITextComponent getName(ItemStack tool) {
    return getFormattedName(tool);
  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return null;
  }

  @Override
  public void onUseTick(World world, LivingEntity entity, ItemStack stack, int partialTicks) {
    ModularToolsMod.LOGGER.debug("EVENT: onUseTick");
  }

  @Override // when right-clicking a block
  public ActionResultType useOn(ItemUseContext context) {
    ModularToolsMod.LOGGER.debug("EVENT: useOn");
    return ActionResultType.PASS;
  }

  @Override // happens every tick when mining a block: getHarvestLevel -> isCorrectToolForDrops -> this
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    ModularToolsMod.LOGGER.debug("EVENT: getDestroySpeed");
    return super.getDestroySpeed(stack, state);
  }

  @Override // happens if useOn defers or fails. Continuously triggers if getUseDuration is 0
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ModularToolsMod.LOGGER.debug("EVENT: use");
    return super.use(world, player, hand);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
    ModularToolsMod.LOGGER.debug("EVENT: finishUsingItem");
    return super.finishUsingItem(stack, world, entity);
  }

  @Override // happens when an attack swing connects
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
    ModularToolsMod.LOGGER.debug("EVENT: hurtEnemy");
    return false;
    //return callAction (new ActionContext(stack, ActionType.ATTACK_ENTITY, target)).equals(ActionResultType.SUCCESS);
  }

  @Override
  public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
    ModularToolsMod.LOGGER.debug("EVENT: mineBlock");
    return super.mineBlock(stack, world, state, pos, entity);
  }

  @Override // happens every tick when mining a block, before getDestroySpeed
  public boolean isCorrectToolForDrops(BlockState state) {
    ModularToolsMod.LOGGER.debug("EVENT: isCorrectToolForDrops");
    return super.isCorrectToolForDrops(state);
  }

  @Override
  public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
    ModularToolsMod.LOGGER.debug("EVENT: interactLivingEntity");
    return super.interactLivingEntity(stack, player, target, hand);
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int intarg, boolean boolarg) {
    super.inventoryTick(stack, world, entity, intarg, boolarg);
  }

  @Override
  public UseAction getUseAnimation(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: getUseAnimation");
    return super.getUseAnimation(stack);
  }

  @Override // happens every tick that right-click is held. Returns 0 by default
  public int getUseDuration(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: getUseDuration");
    return super.getUseDuration(stack);
  }

  @Override
  public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int ticksRemaining) {
    ModularToolsMod.LOGGER.debug("EVENT: releaseUsing");
    super.releaseUsing(stack, world, entity, ticksRemaining);
  }

  @Override // happens every tick on mouseover
  public Rarity getRarity(ItemStack stack) {
    //ModularToolsMod.LOGGER.debug("EVENT: getRarity");
    return super.getRarity(stack);
  }

  @Override
  public Set<ToolType> getToolTypes(ItemStack stack) {
    ModularToolsMod.LOGGER.debug("EVENT: getToolTypes");
    return super.getToolTypes(stack);
  }

  @Override // happens every tick when mining a block, prior to checking isCorrectToolForDrops
  public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    ModularToolsMod.LOGGER.debug("EVENT: getHarvestLevel");
    return super.getHarvestLevel(stack, tool, player, blockState);
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
}
