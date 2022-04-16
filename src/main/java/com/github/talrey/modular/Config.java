package com.github.talrey.modular;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.nio.file.Path;
import java.util.HashMap;

public class Config {
  public static final String CAT_GEN = "General";

  public static ForgeConfigSpec COMMON, CLIENT;

  private static HashMap<String, ForgeConfigSpec.ConfigValue<?>> SETTINGS = new HashMap<>();
  public static String IMBUED_TIER = "Imbued Enchantability";

  static {
    ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
    ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();

    common.comment("General Settings").push(CAT_GEN);
    SETTINGS.put(IMBUED_TIER + "_int", common.comment("How enchantable the Imbued modifier is").define(IMBUED_TIER, 10));
    common.pop();

    COMMON = common.build();
    CLIENT = client.build();
  }

  public static int getIntSetting (String name) {
    if (SETTINGS.containsKey(name + "_int")) return (Integer)(SETTINGS.get(name + "_int").get());
    return 0;
  }

  public static void loadConfig (ForgeConfigSpec spec, Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void onLoad (final ModConfigEvent.Loading loadEvent) {
  }
}
