package com.github.talrey.modular.content;

import com.github.talrey.modular.ModularToolsMod;
import com.github.talrey.modular.api.ToolFactory;
import com.github.talrey.modular.content.items.*;
import com.github.talrey.modular.framework.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistration {
  public static ItemEntry<ModularTool>     TOOL_GENERIC;
  public static ItemEntry<ModularSword>    TOOL_SWORD;
  public static ItemEntry<ModularShield>   TOOL_SHIELD;
  public static ItemEntry<ModularPickaxe>  TOOL_PICKAXE;
  public static ItemEntry<ModularWoodaxe>  TOOL_WOODAXE;
  public static ItemEntry<ModularShovel>   TOOL_SHOVEL;
  public static ItemEntry<ModularHoe>      TOOL_HOE;
  public static ItemEntry<ModularBow>      TOOL_BOW;
  public static ItemEntry<ModularCrossbow> TOOL_CROSSBOW;
  public static ItemEntry<ModularChest>    TOOL_CHEST;

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
  public static ItemEntry<ModularToolComponent> FUNCTION_CROSSBOW;
  public static ItemEntry<ModularToolComponent> FUNCTION_CHEST;

  // == MODIFIERS == //
  public static ItemEntry<MTCModifierImbued>              MODIFIER_IMBUED;
  public static ItemEntry<MTCModifierEverlasting>         MODIFIER_EVERLASTING;
  public static ItemEntry<? extends ModularToolComponent> MODIFIER_PNEUMATIC;   // requires Create Mod to function, generates placeholder otherwise.
  public static ItemEntry<MTCModifierCharged>             MODIFIER_CHARGED;
  public static ItemEntry<ModularToolComponent>           MODIFIER_SCOPED;

  // == supporting data == //
  private static final NonNullUnaryOperator<Item.Properties> defaultToolProperties = p -> p.stacksTo(1).setNoRepair().durability(Tiers.IRON.getUses());

  private static final TagKey<Item> TAG_HANDLE   = makeItemTag(ModularToolsMod.MODID, "handle");
  private static final TagKey<Item> TAG_CORE     = makeItemTag(ModularToolsMod.MODID, "core");
  private static final TagKey<Item> TAG_FUNCTION = makeItemTag(ModularToolsMod.MODID, "function");
  private static final TagKey<Item> TAG_MODIFIER = makeItemTag(ModularToolsMod.MODID, "modifier");

  public ItemRegistration () {
  }

  public static TagKey<Item> makeForgeItemTag (String path) {
    return makeItemTag("forge", path);
  }

  public static TagKey<Item> makeItemTag (String mod, String path) {
    return ForgeRegistries.ITEMS.tags().createOptionalTagKey(new ResourceLocation(mod, path), Collections.emptySet());
  }

  public static IModularTool getModularTool (ItemStack stack) {
    if (stack.getItem() instanceof ModularToolComponent) return getModularTool((ModularToolComponent)stack.getItem());
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
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/" + id.split("_")[0] + "/" + id)))
    .properties(defaultToolProperties)
    .lang(itemName)
    .onRegister(ItemRegistration::registerMTC);
  }

  static InventoryChangeTrigger.TriggerInstance fromTag (TagKey<Item> tag) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
  }

  static InventoryChangeTrigger.TriggerInstance fromItem (Item item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(item);
  }

  public static void registerItems (Registrate reg) {
    boolean createLoaded = ModList.get().isLoaded("create");
    ModularToolsMod.LOGGER.info("Create was " + (createLoaded ? "not " : "") + "detected, compatibility features will " + (createLoaded ? "not " : "") + " function");

    reg.creativeModeTab(()->ITEM_GROUP, "Modular Tools");

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

    FUNCTION_CROSSBOW = component(reg, "function_crossbow", "Crossbow Function", "Crossbow", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    .register();

    FUNCTION_CHEST = component(reg, "function_chest", "Storage Function", "With Storage", ComponentType.FUNCTION)
    .tag(TAG_FUNCTION)
    .register();

    MODIFIER_IMBUED = reg.item("modifier_imbued", p-> new MTCModifierImbued("Imbuing Modifier", p))
    .tag(TAG_MODIFIER)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/modifier/modifier_imbued")))
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
    .register();

    MODIFIER_EVERLASTING = reg.item("modifier_everlasting", p-> new MTCModifierEverlasting("Everlasting Modifier", p))
    .tag(TAG_MODIFIER)
      .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/modifier/modifier_everlasting")))
    .onRegister(ItemRegistration::registerMTC)
    .register();

    MODIFIER_PNEUMATIC = reg.item("modifier_pneumatic", p->
      createLoaded ? new MTCModifierPneumatic("Pneumatic Modifier", p)
      : new ModularToolComponent("Pneumatic Modifier", "Pneumatic", ComponentType.MODIFIER, p)
    )
      .tag(TAG_MODIFIER)
      .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/modifier/modifier_pneumatic")))
      .onRegister(ItemRegistration::registerMTC)
      .register();

    MODIFIER_CHARGED = reg.item("modifier_charged", p-> new MTCModifierCharged("Charged Modifier", p))
      .tag(TAG_MODIFIER)
      .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/modifier/modifier_charged")))
      .onRegister(ItemRegistration::registerMTC)
      .register();

    MODIFIER_SCOPED = component(reg, "modifier_scoped", "Scope Modifier", "Scoped", ComponentType.MODIFIER)
      .tag(TAG_MODIFIER)
      .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/modifier/modifier_scoped")))
      .onRegister(ItemRegistration::registerMTC)
      .register();

    TOOL_GENERIC = reg.item("tool_base", ModularTool::new)
    .properties(defaultToolProperties.andThen(p->p.rarity(Rarity.EPIC)))
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_base")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("How did you get this?")
    .register();

    TOOL_SWORD = reg.item("tool_sword", ModularSword::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_sword")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Sword")
    .register();

    TOOL_SHIELD = reg.item("tool_shield", ModularShield::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_shield")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Shield")
    .register();

    TOOL_PICKAXE = reg.item("tool_pickaxe", ModularPickaxe::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_pickaxe")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Pickaxe")
    .register();

    TOOL_WOODAXE = reg.item("tool_woodaxe", ModularWoodaxe::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_woodaxe")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular woodaxe")
    .register();

    TOOL_SHOVEL = reg.item("tool_shovel", ModularShovel::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_shovel")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Shovel")
    .register();

    TOOL_HOE = reg.item("tool_hoe", ModularHoe::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_hoe")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Hoe")
    .register();

    TOOL_BOW = reg.item("tool_bow", ModularBow::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_bow")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Bow")
    .register();

    TOOL_CROSSBOW = reg.item("tool_crossbow", ModularCrossbow::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_crossbow")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Crossbow")
    .register();

    TOOL_CHEST = reg.item("tool_chest", ModularChest::new)
    .properties(defaultToolProperties)
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_chest")))
    .onRegister(ItemRegistration::registerModularTool)
    .lang("Modular Storage")
    .register();

    ItemEntry<Item> cap = reg.item("captest", ToolFactory.makeTool("captest", BowItem.class))
    .model((ctx,prov)-> prov.generated(ctx, prov.modLoc("item/tool/tool_chest")))
    .onRegister(ToolFactory::register)
    .register();
  }

  public static class MyItemGroup extends CreativeModeTab {
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
  public static final CreativeModeTab ITEM_GROUP = new MyItemGroup(ModularToolsMod.MODID, () -> TOOL_GENERIC.asStack());
}
