package com.github.talrey.modular.content;

import com.github.talrey.modular.content.BlockRegistration;
import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTE;
import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTER;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.TileEntityEntry;

public class TileEntityRegistration {
  public static TileEntityEntry<ToolAssemblerTE> TOOL_ASM;

  public static void registerTileEntities (Registrate reg) {
    TOOL_ASM = reg.tileEntity("tool_assembler", ToolAssemblerTE::new)
    .validBlocks(BlockRegistration.MODULE_ASSEMBLER)
    .renderer(() -> ToolAssemblerTER::new)
    .register();
  }
}
