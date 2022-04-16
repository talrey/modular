package com.github.talrey.modular.content;

import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTE;
import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTER;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class BlockEntityRegistration {
  public static BlockEntityEntry<ToolAssemblerTE> TOOL_ASM;

  public static void registerTileEntities (Registrate reg) {
    TOOL_ASM = reg.blockEntity("tool_assembler", ToolAssemblerTE::new)
    .validBlocks(BlockRegistration.MODULE_ASSEMBLER)
    .renderer(() -> (ctx)-> new ToolAssemblerTER())
    .register();
  }
}
