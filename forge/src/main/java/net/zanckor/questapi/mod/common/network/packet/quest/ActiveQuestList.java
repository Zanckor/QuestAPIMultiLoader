package net.zanckor.questapi.mod.common.network.packet.quest;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.zanckor.questapi.util.GsonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ActiveQuestList {
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

    public ActiveQuestList(FriendlyByteBuf buffer) {
        listSize = buffer.readInt();

        for (int i = 0; i < listSize; i++) {
            questFileList.add(buffer.readUtf());
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(questFileList.size());

        for (String s : questFileList) {
            buffer.writeUtf(s);
        }
    }


    public static void handler(ActiveQuestList msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            List<UserQuest> userQuestList = new ArrayList<>();
            msg.questFileList.forEach(questFile -> {
                try {
                    userQuestList.add((UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.setActiveQuestList(userQuestList));
        });

        ctx.get().setPacketHandled(true);
    }
}
