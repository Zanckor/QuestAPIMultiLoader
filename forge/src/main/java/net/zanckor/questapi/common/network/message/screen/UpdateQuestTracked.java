package net.zanckor.questapi.common.network.message.screen;

import net.zanckor.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.common.network.handler.ClientHandler;
import net.zanckor.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

public class UpdateQuestTracked {
    private String userQuest;


    public UpdateQuestTracked(UserQuest userQuest) {
        this.userQuest = GsonManager.gson.toJson(userQuest);
    }

    public UpdateQuestTracked(FriendlyByteBuf buffer) {
        userQuest = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(userQuest);
    }


    public static void handler(UpdateQuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                File file = File.createTempFile("userQuest", "json");
                Files.writeString(file.toPath(), msg.userQuest);
                UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

                if(userQuest != null) DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.updateQuestTracked(userQuest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

