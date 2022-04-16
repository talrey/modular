package com.github.talrey.modular.content;

import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;

public class BlockRegistration {
  public static BlockEntry<ToolAssemblerBlock> MODULE_ASSEMBLER;

  public BlockRegistration () {
  }

  public static void registerBlocks (Registrate reg) {
    reg.creativeModeTab(()->ItemRegistration.ITEM_GROUP);

    MODULE_ASSEMBLER = reg.block("module_assembler", ToolAssemblerBlock::new)
    .properties(p-> p.strength(1,4))
      .tag(BlockTags.MINEABLE_WITH_AXE)
    .lang("Modular Tool Bench")
    .simpleItem()
    .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
      .pattern("nnn")
      .pattern(" c ")
      .define('n', Items.IRON_NUGGET)
      .define('c', Items.CRAFTING_TABLE)
      .unlockedBy("has_item", ItemRegistration.fromItem(Items.CRAFTING_TABLE))
      .save(prov))
    .register();
  }
}
