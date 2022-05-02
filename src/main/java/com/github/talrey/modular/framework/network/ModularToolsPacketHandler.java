package com.github.talrey.modular.framework.network;

import com.github.talrey.modular.ModularToolsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModularToolsPacketHandler {
  private static final String PROTOCOL_VERSION = "1";
  private static final ResourceLocation CHANNEL_ID = new ResourceLocation(ModularToolsMod.MODID, "main");

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
    CHANNEL_ID, ()-> PROTOCOL_VERSION,
    PROTOCOL_VERSION::equals,
    PROTOCOL_VERSION::equals
  );

  public static void register() {
    int uid = 0;
    CHANNEL.registerMessage(uid++, PacketUpdateAssembler.class, PacketUpdateAssembler::encode, PacketUpdateAssembler::decode, PacketUpdateAssembler::handle);
    CHANNEL.registerMessage(uid++, PacketUpdateTool.class,      PacketUpdateTool::encode,      PacketUpdateTool::decode,      PacketUpdateTool::handle);
  }

//  public static void sendToServer (CompoundTag data) {
//    CHANNEL.sendToServer(
//  }

  public static void updateToolAssemblerClientside (CompoundTag data, Level world, BlockPos pos) {
    CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(()-> world.getChunkAt(pos)), PacketUpdateAssembler.toClient(data, pos));
  }

  public static void updateToolClientSide (CompoundTag data, ServerPlayer player, int slot) {
CHANNEL.send(PacketDistributor.PLAYER.with(()-> player), PacketUpdateTool.toClient(data, slot));
  }
}
