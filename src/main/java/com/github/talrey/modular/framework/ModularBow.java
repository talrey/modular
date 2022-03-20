package com.github.talrey.modular.framework;

import com.github.talrey.modular.content.ItemRegistration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ModularBow extends BowItem implements IModularTool {
  public ModularBow(Properties props) {
    super(props);
  }

  @Override
  public ModularToolComponent getFunctionComponent() {
    return ItemRegistration.FUNCTION_BOW.get();
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
