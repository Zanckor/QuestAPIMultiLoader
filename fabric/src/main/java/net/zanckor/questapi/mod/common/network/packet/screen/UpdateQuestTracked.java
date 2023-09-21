package net.zanckor.questapi.mod.common.network.packet.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Environment(EnvType.CLIENT)
public class UpdateQuestTracked extends AbstractPacket {
    String userQuestJson;

    public UpdateQuestTracked(UserQuest userQuest) {
        this.userQuestJson = GsonManager.gson.toJson(userQuest);
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeUtf(userQuestJson);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.UPDATE_QUEST_TRACKED;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        String userQuestJson = buffer.readUtf();


        try {
            File file = File.createTempFile("userQuest", "json");
            Files.writeString(file.toPath(), userQuestJson);
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

            if (userQuest != null)
                client.execute(() -> handler(userQuest));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handler(UserQuest userQuest) {
        NetworkClientHandler.updateQuestTracked(userQuest);
    }
}
