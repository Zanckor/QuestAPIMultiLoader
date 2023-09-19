package net.zanckor.questapi.mod.common.network.packet.quest;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions, unused")
public class TimerPacket {

    public TimerPacket() {
    }

    public TimerPacket(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(TimerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ServerHandler.questTimer(ctx.get().getSender().serverLevel().getLevel()));

        ctx.get().setPacketHandled(true);
    }
}

