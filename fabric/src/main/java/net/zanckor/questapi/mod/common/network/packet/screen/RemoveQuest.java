package net.zanckor.questapi.mod.common.network.packet.screen;

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
public class RemoveQuest extends AbstractPacket {
    String id;

    public RemoveQuest(String id) {
        this.id = id;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeUtf(id);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.REMOVE_QUEST;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        String id = buffer.readUtf();

        client.execute(() -> handler(id));
    }

    private static void handler(String id) {
        NetworkClientHandler.removeQuest(id);
    }
}
