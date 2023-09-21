package net.zanckor.questapi.mod.common.network.packet.quest;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ActiveQuestList extends AbstractPacket {

    List<String> questFileList = new ArrayList<>();
    int listSize;

    public ActiveQuestList(UUID playerUUID) {
        try {
            File[] activeQuests = CommonMain.getActiveQuest(CommonMain.getUserFolder(playerUUID)).toFile().listFiles();

            if (activeQuests == null) return;

            for (File file : activeQuests) {
                questFileList.add(Files.readString(file.toPath()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();

        buffer.writeInt(questFileList.size());

        for (String s : questFileList) {
            buffer.writeUtf(s);
        }

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.ACTIVE_QUEST_LIST;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        List<String> questFileList = new ArrayList<>();
        int listSize = buffer.readInt();

        for (int i = 0; i < listSize; i++) {
            questFileList.add(buffer.readUtf());
        }

        client.execute(() -> handler(questFileList));
    }

    private static void handler(List<String> questFileList) {
        List<UserQuest> userQuestList = new ArrayList<>();
        questFileList.forEach(questFile -> {
            try {
                userQuestList.add((UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        NetworkClientHandler.setActiveQuestList(userQuestList);
    }
}
