package net.zanckor.questapi.mod.common.network.packet.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.zanckor.questapi.commonutil.GsonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

public class UpdateQuestTracked {
    private final String userQuestJson;


    public UpdateQuestTracked(UserQuest userQuest) {
        this.userQuestJson = GsonManager.gson.toJson(userQuest);
    }

    public UpdateQuestTracked(FriendlyByteBuf buffer) {
        userQuestJson = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(userQuestJson);
    }


    public static void handler(UpdateQuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                File file = File.createTempFile("userQuest", "json");
                Files.writeString(file.toPath(), msg.userQuestJson);
                UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

                if (userQuest != null)
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.updateQuestTracked(userQuest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

