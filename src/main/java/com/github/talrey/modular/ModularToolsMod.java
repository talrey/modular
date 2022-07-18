package com.github.talrey.modular;

import com.github.talrey.modular.api.capability.ItemStorageProvider;
import com.github.talrey.modular.content.BlockRegistration;
import com.github.talrey.modular.content.ItemRegistration;
import com.github.talrey.modular.content.BlockEntityRegistration;
import com.github.talrey.modular.content.items.MTCModifierCharged;
import com.github.talrey.modular.framework.IModularTool;
import com.github.talrey.modular.framework.MTCEnergyStorage;
import com.github.talrey.modular.framework.ModularScopeOverlay;
import com.github.talrey.modular.framework.ModularToolComponent;
import com.tterrag.registrate.Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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

    registerOverlays();
  }

  private static void registerOverlays () {
    OverlayRegistry.registerOverlayAbove(ForgeIngameGui.SPYGLASS_ELEMENT, "Modular's Scope", ModularScopeOverlay.GETTER);
  }

  @SubscribeEvent
  public void AddModularCapabilities (AttachCapabilitiesEvent<ItemStack> event) {
    if (event.getObject().getItem() instanceof IModularTool) {
      for (ModularToolComponent mtc : IModularTool.getAllComponents(event.getObject())) {
        if (mtc instanceof MTCModifierCharged) {
          event.addCapability(new ResourceLocation(MODID, "energy"), new MTCEnergyStorage(event.getObject()));
        }
      }
    }
  }

  @SubscribeEvent
  public void ZoomInEvent (FOVModifierEvent event) {
    Player player = event.getEntity();
    if (player.isUsingItem() && IModularTool.hasComponent(player.getUseItem(), ItemRegistration.MODIFIER_SCOPED.get())) {
      event.setNewfov(0.1f);
    }
  }

  @SubscribeEvent
  public void RegisterCapabilities (RegisterCapabilitiesEvent event) {
    event.register(ItemStorageProvider.class);
  }
}
