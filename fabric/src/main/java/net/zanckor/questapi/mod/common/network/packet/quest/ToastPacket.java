package net.zanckor.questapi.mod.common.network.packet.quest;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

@Environment(EnvType.CLIENT)
public class ToastPacket extends AbstractPacket {
    String questName;

    public ToastPacket(String questName) {
        this.questName = questName;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeUtf(questName);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.TOAST_PACKET;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        String questName = buffer.readUtf();

        client.execute(() -> handler(questName));
    }

    private static void handler(String questName) {
        NetworkClientHandler.toastQuestCompleted(questName);
    }
}
