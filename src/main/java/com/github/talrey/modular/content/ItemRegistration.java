package com.github.talrey.modular.content;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.content.items.MTCModifierImbued;
import com.github.talrey.modular.framework.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class ItemRegistration {
  public static ItemEntry<ModularTool>    TOOL_GENERIC;
  public static ItemEntry<ModularSword>   TOOL_SWORD;
  public static ItemEntry<ModularShield>  TOOL_SHIELD;
  public static ItemEntry<ModularPickaxe> TOOL_PICKAXE;
  public static ItemEntry<ModularWoodaxe> TOOL_WOODAXE;
  public static ItemEntry<ModularShovel>  TOOL_SHOVEL;
  public static ItemEntry<ModularHoe>     TOOL_HOE;
  public static ItemEntry<ModularBow>     TOOL_BOW;

  private static HashMap<ModularToolComponent, IModularTool> allModularTools = new HashMap<>();
  private static ArrayList<ModularToolComponent> allComponents = new ArrayList<>();

  // == HANDLES == //
  public static ItemEntry<ModularToolComponent> HANDLE_LONG;

  // == CORES == //
  public static ItemEntry<ModularToolComponent> CORE_SINGLE;

  // == FUNCTIONS == //
  public static ItemEntry<ModularToolComponent> FUNCTION_BLADE;
  public static ItemEntry<ModularToolComponent> FUNCTION_SHIELD;
  public static ItemEntry<ModularToolComponent> FUNCTION_PICKAXE;
  public static ItemEntry<ModularToolComponent> FUNCTION_WOODAXE;
  public static ItemEntry<ModularToolComponent> FUNCTION_SHOVEL;
  public static ItemEntry<ModularToolComponent> FUNCTION_HOE;
  public static ItemEntry<ModularToolComponent> FUNCTION_BOW;

  // == MODIFIERS == //
  public static ItemEntry<MTCModifierImbued> MODIFIER_IMBUED;

  // == supporting data == //
  private static final NonNullUnaryOperator<Item.Properties> defaultToolProperties = p -> p.stacksTo(1).setNoRepair();

  private static final ITag.INamedTag<Item> TAG_HANDLE   = ItemTags.bind( (new ResourceLocation(ModularToolsMod.MODID, "handle")).toString() );
  private static final ITag.INamedTag<Item> TAG_CORE     = ItemTags.bind( (new ResourceLocation(ModularToolsMod.MODID, "core")).toString() );
  private static final ITag.INamedTag<Item> TAG_FUNCTION = ItemTags.bind( (new ResourceLocation(ModularToolsMod.MODID, "function")).toString() );
  private static final ITag.INamedTag<Item> TAG_MODIFIER = ItemTags.bind( (new ResourceLocation(ModularToolsMod.MODID, "modifier")).toString() );

  public ItemRegistration () {
  }

  public static IModularTool getModularTool (ItemStack stack) {
    if (stack.getItem() instanceof ModularToolComponent mtc) return getModularTool(mtc);
    /*else*/ return TOOL_GENERIC.get();
  }

  public static IModularTool getModularTool (ModularToolComponent mtc) {
    return allModularTools.getOrDefault(mtc, TOOL_GENERIC.get());
  }

  public static void registerModularTool (IModularTool tool) {
    allModularTools.put(tool.getFunctionComponent(), tool);
  }

  public static ModularToolComponent getMTC (int index) {
    if (0 <= index && index < allComponents.size()) return allComponents.get(index);
    /*else*/ return null;
  }

  public static int getIndexOfMTC (ModularToolComponent mtc) {
    return allComponents.indexOf(mtc);
  }

  private static void registerMTC (ModularToolComponent mtc) {
    allComponents.add(mtc);
  }

  private static ItemBuilder<ModularToolComponent, ?> component (Registrate reg, String id, String itemName, String partName, ComponentType type) {
    return reg.item(id, p-> new ModularToolComponent(itemName, partName, type, p))
    .properties(defaultToolProperties)
    .lang(itemName)
    .onRegister(ItemRegistration::registerMTC);
  }

  static InventoryChangeTrigger.Instance fromTag (ITag tag) {
    return InventoryChangeTrigger.Instance.hasItems(ItemPredicate.Builder.item().of(tag).build());
  }

  static InventoryChangeTrigger.Instance fromItem (Item item) {
    return InventoryChangeTrigger.Instance.hasItems(item);
  }

  public static void registerItems (Registrate reg) {
    reg.itemGroup(()->ITEM_GROUP, "Modular Tools");

    //reg.itemGroup(()->ITEM_GROUP, "Modular Tools");

    HANDLE_LONG = component(reg,"handle_long", "Long Handle", "Long-handled", ComponentType.HANDLE)
    .tag(TAG_HANDLE)
    .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
      .pattern(" s")
      .pattern("s ")
      .define('s', Items.STICK)
      .unlockedBy("has_item", fromItem(Items.STICK))
      .save(prov)
    )
    .register();

    CORE_SINGLE = component(reg, "core_single", "Single Core", "", ComponentType.CORE)
    .tag(TAG_CORE)
    .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
      .pattern("sns")
      .pattern("n n")
      .pattern("sns")
      .define('s', Tags.Items.STRING)
      .define('n', Tags.Items.NUGGETS_IRON)
      .unlockedBy("has_item", fromTag(Tags.Items.STRING))
      .save(prov)
    )
    .register();

    FUNCTION_BLADE = component(reg, "function_blade", "Blade Function", "Blade", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    .recipe((ctx, prov)-> ShapedRecipeBuilder.shaped(ctx.get())
      .pattern("n")
      .pattern("i")
      .pattern("n")
      .define('n', Tags.Items.NUGGETS_IRON)
      .define('i', Tags.Items.INGOTS_IRON)
      .unlockedBy("has_item", fromTag(Tags.Items.NUGGETS_IRON))
      .save(prov)
    )
    //.onRegister( mtc -> mtc.subscribe(ActionType.ON_ATTACK, (ctx)-> {
    //  ModularToolsMod.LOGGER.debug("doing attack");
    //  return true;
    //}))
    .register();

    FUNCTION_SHIELD = component(reg, "function_shield", "Shield Function", "Buckler", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    FUNCTION_PICKAXE = component(reg, "function_pickaxe", "Pickaxe Function", "Pick", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    FUNCTION_WOODAXE = component(reg, "function_woodaxe", "Woodaxe Function", "Axe", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    FUNCTION_SHOVEL = component(reg, "function_shovel", "Shovel Function", "Shovel", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    FUNCTION_HOE = component(reg, "function_hoe", "Hoe Function", "Hoe", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    FUNCTION_BOW = component(reg, "function_bow", "Bow Function", "Bow", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    // .recipe
    .register();

    MODIFIER_IMBUED = reg.item("modifier_imbued", p-> new MTCModifierImbued("Imbuing Modifier", p))
    .tag(TAG_MODIFIER)
    .recipe((ctx, prov)-> ShapedRecipeBuilder.shaped(ctx.get())
      .pattern("g g")
      .pattern(" b ")
      .pattern("g g")
      .define('g', Tags.Items.INGOTS_GOLD)
      .define('b', Items.BOOK)
      .unlockedBy("has_item", fromItem(Items.BOOK))
      .save(prov)
    )
    .onRegister(ItemRegistration::registerMTC)
    /*
    .onRegister( mtc -> {
      mtc.subscribe(ActionType.ASSEMBLE, (ctx)-> {
        ctx.toolInUse.enchant(Enchantments.BINDING_CURSE, 2);
        return true;
      });
      mtc.subscribe(ActionType.DISASSEMBLE, (ctx)-> {
        ctx.data = MODIFIER_IMBUED.asStack();
        EnchantmentHelper.getEnchantments(ctx.toolInUse).forEach((enchant, level)-> ((ItemStack)ctx.data).enchant(enchant, level));
        ctx.toolInUse.removeTagKey("Enchantments");
        return true;
      });
      ItemRegistration.registerMTC(mtc);
    })
    */
    .register();

    TOOL_GENERIC = reg.item("tool_base", ModularTool::new)
    .properties(defaultToolProperties.andThen(p->p.rarity(Rarity.EPIC)))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("How did you get this?")
    .register();

    TOOL_SWORD = reg.item("tool_sword", ModularSword::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Sword")
    .register();

    TOOL_SHIELD = reg.item("tool_shield", ModularShield::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Shield")
    .register();

    TOOL_PICKAXE = reg.item("tool_pickaxe", ModularPickaxe::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Pickaxe")
    .register();

    TOOL_WOODAXE = reg.item("tool_woodaxe", ModularWoodaxe::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular woodaxe")
    .register();

    TOOL_SHOVEL = reg.item("tool_shovel", ModularShovel::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Shovel")
    .register();

    TOOL_HOE = reg.item("tool_hoe", ModularHoe::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Hoe")
    .register();

    TOOL_BOW = reg.item("tool_bow", ModularBow::new)
    .properties(defaultToolProperties)
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Bow")
    .register();
  }

  public static class MyItemGroup extends ItemGroup {
    private final Supplier<ItemStack> sup;

    public MyItemGroup (final String name, final Supplier<ItemStack> supplier) {
      super(name);
      sup = supplier;
    }
    @Override
    public ItemStack makeIcon () {
      return sup.get();
    }
  }
  public static final ItemGroup ITEM_GROUP = new MyItemGroup(ModularToolsMod.MODID, () -> TOOL_GENERIC.asStack());
}
