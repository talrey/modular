package com.github.talrey.modular.framework;

import com.github.talrey.modular.Util;
import com.github.talrey.modular.framework.capability.CapabilityNotPresentException;
import com.github.talrey.modular.framework.capability.IModularToolSystem;
import com.github.talrey.modular.framework.capability.ModularToolCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public interface IModularTool {

  static ICapabilityProvider onInitCapabilities (ItemStack stack, @Nullable CompoundTag tag) {
    return new ModularToolCapability();
  }

  ModularToolComponent getFunctionComponent ();

  default InteractionResultHolder<ItemStack> cycle (Player user, ItemStack held) {
    return InteractionResultHolder.success(held.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new).cycleFunction(user, held));
  }

  static IDurabilityConverter getDurabilityModule (ItemStack stack, Entity wielder, boolean skipEmpty) {
    ModularToolComponent[] modules = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new).getComponents();
    for (ModularToolComponent mtc : modules) {
      if (mtc instanceof IDurabilityConverter) {
        if ( ((IDurabilityConverter)mtc).getCurrentCapacity(stack, wielder) <= 0 && skipEmpty) continue;
        return (IDurabilityConverter)mtc;
      }
    }
    return null;
  }

  static void absorbDamage (ItemStack stack, int amount, Entity wielder) {
    int remaining = amount;
    IModularToolSystem cap = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);
    ModularToolComponent[] modules = cap.getComponents();
    for (ModularToolComponent mtc : modules) {
      if (mtc instanceof IDurabilityConverter && ((IDurabilityConverter)mtc).canConsume(stack, amount, wielder)) {
        remaining = ((IDurabilityConverter)mtc).consume(stack, amount, wielder);
      }
    }
    cap.damageTool(remaining);
  }

  @OnlyIn(Dist.CLIENT)
  static boolean isBarVisible (ItemStack stack) {
    LocalPlayer player = Minecraft.getInstance().player;
    IModularToolSystem cap = stack.getCapability(ModularToolCapability.MTS).orElse(null); //Throw(CapabilityNotPresentException::new);
    if (cap == null) return false;
    IDurabilityConverter mtc = getDurabilityModule(stack, player, true);
    if (mtc != null) return mtc.isLayerBarVisible(stack, player);
    /*else*/
    return cap.getDamage() > 0;
  }

  @OnlyIn(Dist.CLIENT)
  static int getBarWidth (ItemStack stack) {
    LocalPlayer player = Minecraft.getInstance().player;
    IModularToolSystem cap = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);
    IDurabilityConverter mtc = getDurabilityModule(stack, player, true);
    if (mtc != null) return mtc.getLayerBarWidth(stack, player);
    /*else*/
    return Math.round(13.0F - (float) cap.getDamage() / cap.getMaxDamage() * 13.0F);
  }

  @OnlyIn(Dist.CLIENT)
  static int getBarColor (ItemStack stack) {
    LocalPlayer player = Minecraft.getInstance().player;
    IDurabilityConverter mtc = getDurabilityModule(stack, player, true);
    if (mtc != null) return mtc.getLayerBarColor(stack);
    /*else*/
    return Mth.hsvToRgb(Math.max(0.0F, 1.0F - (float) stack.getDamageValue() / stack.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
  }

  static String getToolName (ItemStack tool) {
    IModularToolSystem cap = tool.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);

    ModularToolComponent handle = Util.castIfPresent(cap.findComponents(ComponentType.CORE)[0]);
    ModularToolComponent core   = Util.castIfPresent(cap.findComponents(ComponentType.HANDLE)[0]);
    ItemStack[] funcs           = cap.findComponents(ComponentType.FUNCTION);
    ItemStack[] mods            = cap.findComponents(ComponentType.MODIFIER);

    if (handle == null || core == null) return "Unknown Modular Tool";

    StringBuilder strb = new StringBuilder();
    strb.append(handle.partName);
    if (!core.partName.isEmpty()) strb.append(' ');
    strb.append(core.partName);

    for (ItemStack m : mods) {
      if (m.isEmpty()) continue;
      strb.append(' ');
      strb.append( ((ModularToolComponent)m.getItem()).partName );
    }
    for (ItemStack f : funcs) {
      if (f.isEmpty()) continue;
      strb.append(' ');
      strb.append( ((ModularToolComponent)f.getItem()).partName );
    }
    return strb.toString().trim();
  }

  default Component getFormattedName (ItemStack stack) {
    return new TextComponent(getToolName(stack)).withStyle(stack.getRarity().color);
  }

  static CompoundTag getShareTag (ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    IModularToolSystem mts = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);
    tag.put("mts", mts.serializeNBT());
    return tag;
  }

  static void readShareTag (ItemStack stack, @Nullable CompoundTag nbt) {
    if (nbt != null && nbt.contains("mts")) {
      IModularToolSystem mts = stack.getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new);
      mts.deserializeNBT(nbt.getCompound("mts"));
    }
  }
}
