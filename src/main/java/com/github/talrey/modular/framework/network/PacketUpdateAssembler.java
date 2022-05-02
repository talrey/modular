package com.github.talrey.modular.framework.network;

import com.github.talrey.modular.content.blocks.assembler.ToolAssemblerTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateAssembler {
  protected CompoundTag payload;
  protected Level level;
  protected BlockPos pos;


  private PacketUpdateAssembler (CompoundTag data, BlockPos pos) {
    payload = data;
    this.pos = pos;
  }

  static PacketUpdateAssembler toClient (CompoundTag data, BlockPos pos) { return new PacketUpdateAssembler(data, pos); }

  public static void encode (PacketUpdateAssembler packet, FriendlyByteBuf buf) {
    buf.writeNbt(packet.payload);
    buf.writeBlockPos(packet.pos);
  }

  public static PacketUpdateAssembler decode (FriendlyByteBuf buf) {
    return new PacketUpdateAssembler(buf.readNbt(), buf.readBlockPos()) {
    };
  }

  public static void handle (PacketUpdateAssembler packet, Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(()-> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> ()-> PacketUpdateAssembler.__handle(packet, supplier));
    });
    supplier.get().setPacketHandled(true);
  }

  private static void __handle (PacketUpdateAssembler packet, Supplier<NetworkEvent.Context> supplier) {
    Level local = Minecraft.getInstance().level;
    if (local != null && local.getBlockEntity(packet.pos) instanceof ToolAssemblerTE tate) {
      tate.deserializeNBT(packet.payload);
    }
  }
}
