package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ModularWoodaxe extends AxeItem implements IModularTool {
  public ModularWoodaxe (Properties props) {
    this(ItemTier.WOOD, 1, 1f, props);
  }

  public ModularWoodaxe (IItemTier itemTier, int baseDamage, float baseAttackSpeed, Properties props) {
    super(itemTier, baseDamage, baseAttackSpeed, props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return ItemRegistration.FUNCTION_WOODAXE.get();
  }

  @Override
  public ITextComponent getName(ItemStack tool) {
    if (tool.getTag() != null && tool.getTag().contains(NBT_TAG)) return getFormattedName(tool);
    /*else*/ return super.getName(tool);
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (user.isShiftKeyDown()) {
      return ActionResult.success(IModularTool.cycleFunctions(user.getItemInHand(hand)));
    }
    return super.use(world, user, hand);
  }
}
