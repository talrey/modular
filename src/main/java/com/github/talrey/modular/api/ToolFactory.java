package com.github.talrey.modular.api;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.api.capability.ItemStorageProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashSet;

public class ToolFactory {
  private static HashSet<Item> toolTypes = new HashSet<Item>();

  public static final ResourceLocation CAPABILITY = new ResourceLocation(ModularToolsMod.MODID, "modulartool");

  public static <T extends Item> NonNullFunction<Item.Properties, Item> makeTool (String name, Class<T> supertype) {
    try {
      Constructor<T> c = supertype.getDeclaredConstructor(Item.Properties.class);
    } catch (NoSuchMethodException e) {
      ModularToolsMod.LOGGER.error("Unable to get constructor for type " + supertype.getName());
      return Item::new;
    }
    return (p) -> new Item(p) {
      @Nullable
      @Override
      public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemStorageProvider();
      }
    };
  }

  public static void register (Item type) {
    if (!isRegistered(type)) toolTypes.add(type);
  }

  public static boolean isRegistered (Item type) {
    return toolTypes.contains(type);
  }

  public static  boolean isRegistered (ItemStack stack) {
    return isRegistered(stack.getItem());
  }
}
