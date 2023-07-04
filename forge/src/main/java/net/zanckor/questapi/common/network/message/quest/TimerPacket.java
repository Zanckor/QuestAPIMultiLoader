package net.zanckor.questapi.common.network.message.quest;

import net.zanckor.questapi.common.network.handler.ServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TimerPacket {

    public TimerPacket() {
    }

    public TimerPacket(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(TimerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerHandler.questTimer(ctx.get().getSender().getLevel());
        });

        ctx.get().setPacketHandled(true);
    }
}

