package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.util.UtilRemote;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class KeybindCurioMessage {

  public KeybindCurioMessage() {}

  public static void handle(KeybindCurioMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      ServerLevel serverWorld = (ServerLevel) player.level();
      UtilRemote.searchAndOpen(player, serverWorld);
    });
    ctx.get().setPacketHandled(true);
  }

  public static KeybindCurioMessage decode(FriendlyByteBuf buf) {
    KeybindCurioMessage message = new KeybindCurioMessage();
    return message;
  }

  public static void encode(KeybindCurioMessage msg, FriendlyByteBuf buf) {
    //    buf.writeBoolean(msg.direction);
  }
}
