package net.zanckor.questapi.mod.common.network.packet.screen;

import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions, unused")
public class RequestActiveQuests {

    public RequestActiveQuests() {
    }

    public RequestActiveQuests(FriendlyByteBuf buffer) {

    }

    public void encodeBuffer(FriendlyByteBuf buffer) {

    }


    public static void handler(RequestActiveQuests msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
        });

        ctx.get().setPacketHandled(true);
    }
}

