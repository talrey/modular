package com.github.talrey.modular;

import com.github.talrey.modular.content.BlockRegistration;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.content.TileEntityRegistration;
import com.tterrag.registrate.Registrate;
import net.minecraftforge.common.MinecraftForge;
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
  private static TileEntityRegistration tiles;

  public ModularToolsMod () {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);

//    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
//    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
//    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
//    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(this);

    registrar = Registrate.create(MODID);
    items = new ItemRegistration();
    items.registerItems(registrar);
    blocks = new BlockRegistration();
    blocks.registerBlocks(registrar);
    tiles = new TileEntityRegistration();
    tiles.registerTileEntities(registrar);
  }

//  private void setupCommon (final FMLCommonSetupEvent event) {
    // register capabilities here
//  }

//  private void setupClient (final FMLClientSetupEvent event) {
//    LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
//  }

//  private void enqueueIMC(final InterModEnqueueEvent event)
//  {
//    // some example code to dispatch IMC to another mod
//    InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
//  }

//  private void processIMC(final InterModProcessEvent event)
//  {
//    // some example code to receive and process InterModComms from other mods
//    LOGGER.info("Got IMC {}", event.getIMCStream().
//    map(m->m.getMessageSupplier().get()).
//    collect(Collectors.toList()));
//  }
}
