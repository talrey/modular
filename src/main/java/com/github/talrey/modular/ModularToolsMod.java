package com.github.talrey.modular;

import com.github.talrey.modular.content.BlockRegistration;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.content.BlockEntityRegistration;
import com.github.talrey.modular.framework.capability.ModularToolCapability;
import com.github.talrey.modular.framework.network.ModularToolsPacketHandler;
import com.tterrag.registrate.Registrate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("modular")
public class ModularToolsMod
{
  public static final String MODID = "modular";
  public static final Logger LOGGER = LogManager.getLogger();

  private static Registrate registrar;
  private static ItemRegistration items;
  private static BlockRegistration blocks;
  private static BlockEntityRegistration tiles;

  public ModularToolsMod () {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);
    MinecraftForge.EVENT_BUS.register(this);

    registrar = Registrate.create(MODID);
    items = new ItemRegistration();
    items.registerItems(registrar);
    blocks = new BlockRegistration();
    blocks.registerBlocks(registrar);
    tiles = new BlockEntityRegistration();
    tiles.registerTileEntities(registrar);

    ModularToolsPacketHandler.register();
  }

  @SubscribeEvent
  public void registerCapabilities (RegisterCapabilitiesEvent event) {
    event.register(ModularToolCapability.class);
  }

//  @SubscribeEvent
//  public void AddModularCapabilities (AttachCapabilitiesEvent<ItemStack> event) {
//    if (event.getObject().getItem() instanceof IModularTool) {
//      for (ItemStack mod :
//        event.getObject().getCapability(ModularToolCapability.MTS).orElseThrow(CapabilityNotPresentException::new).findComponents(ComponentType.MODIFIER)) {
//        if (mod.getItem() instanceof MTCModifierCharged) {
//          event.addCapability(new ResourceLocation(MODID, "energy"), new MTCEnergyStorage(event.getObject()));
//        }
//      }
//    }
//  }
}
