package com.github.talrey.modular.content.items;

import com.github.talrey.modular.framework.ComponentType;
import com.github.talrey.modular.framework.IDurabilityConverter;
import com.github.talrey.modular.framework.ModularToolComponent;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MTCModifierPneumatic extends ModularToolComponent implements IDurabilityConverter {
  private static final int USES_PER_TANK = 200; // TODO move to a Config

  public MTCModifierPneumatic (String name, Properties props) {
    super(name, "Pneumatic", ComponentType.MODIFIER, props);
  }

  @Override
  public boolean canConsume(ItemStack stack, int amount, Entity wielder) {
    if (wielder instanceof LivingEntity) {
      ItemStack tank = BackTankUtil.get((LivingEntity)wielder);
      return BackTankUtil.hasAirRemaining(tank);
    }
    else return false;
  }

  @Override
  public int consume(ItemStack stack, int amount, Entity wielder) {
    if (wielder instanceof LivingEntity) {
      if (BackTankUtil.canAbsorbDamage((LivingEntity)wielder, USES_PER_TANK)) {
        return 0;
      }
    }
    return amount;
  }

  @Override
  public int getMaxCapacity(ItemStack stack, Entity wielder) {
    if (wielder instanceof LivingEntity) {
      ItemStack tank = BackTankUtil.get((LivingEntity) wielder);
      if (stack.isEmpty()) return 0;

      return BackTankUtil.maxAir(tank);
    }
    return 0;
  }

  @Override
  public int getCurrentCapacity(ItemStack stack, Entity wielder) {
    if (wielder instanceof LivingEntity) {
      ItemStack tank = BackTankUtil.get((LivingEntity)wielder);
      if (stack.isEmpty()) return 0;

      return Math.round(BackTankUtil.getAir(tank));
    }
    return 0;
  }

  @Override
  public int recharge(ItemStack stack, int amount, Entity wielder) {
    return amount; // cannot recharge
  }
}
