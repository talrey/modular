package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ModularSword extends SwordItem implements IModularTool {

  // default makes it pretty weak
  public ModularSword (Properties props) {
    this(ItemTier.WOOD, 1, 1f, props);
  }

  public ModularSword (IItemTier itemTier, int baseDamage, float baseAttackSpeed, Properties props) {
    super(itemTier, baseDamage, baseAttackSpeed, props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() { return ItemRegistration.FUNCTION_BLADE.get(); }

  @Override
  public ITextComponent getName(ItemStack tool) {
    return getFormattedName(tool);
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (user.isShiftKeyDown()) {
      return ActionResult.success(IModularTool.cycleFunctions(user.getItemInHand(hand)));
    }
    return super.use(world, user, hand);
  }
}
