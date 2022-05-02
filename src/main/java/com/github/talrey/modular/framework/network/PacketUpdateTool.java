package com.github.talrey.modular.framework.network;

import com.github.talrey.modular.framework.capability.ModularToolCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateTool {
  protected CompoundTag payload;
  protected int itemSlot;

  private PacketUpdateTool (CompoundTag data, int slot) {
    this.payload = data;
    this.itemSlot = slot;
  }

  static PacketUpdateTool toClient (CompoundTag data, int slot) { return new PacketUpdateTool(data, slot); }

  public static void encode (PacketUpdateTool packet, FriendlyByteBuf buf) {
    buf.writeNbt(packet.payload);
    buf.writeInt(packet.itemSlot);
  }

  public static PacketUpdateTool decode (FriendlyByteBuf buf) {
    return new PacketUpdateTool(buf.readNbt(), buf.readInt());
  }

  public static void handle (PacketUpdateTool packet, Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(()-> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> ()-> PacketUpdateTool.__handle(packet, supplier));
    });
    supplier.get().setPacketHandled(true);
  }

  private static void __handle (PacketUpdateTool packet, Supplier<NetworkEvent.Context> supplier) {
    Player local = Minecraft.getInstance().player;
    if (local != null) {
      ItemStack tool = local.getInventory().getItem(packet.itemSlot);
      if (tool.getCapability(ModularToolCapability.MTS).isPresent()) {
        tool.deserializeNBT(packet.payload);
        local.getInventory().setItem(packet.itemSlot, tool);
      }
    }
  }
}
