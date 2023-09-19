package net.zanckor.questapi.mod.common.network.packet.screen;

import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemovedQuest {
    private final String id;


    public RemovedQuest(String questID) {
        this.id = questID;
    }

    public RemovedQuest(FriendlyByteBuf buffer) {
        id = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(id);
    }


    public static void handler(RemovedQuest msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.removeQuest(msg.id)));

        ctx.get().setPacketHandled(true);
    }
}

