package net.zanckor.questapi.mod.common.network.packet.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;

import java.io.IOException;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModifyTrackedQuests {
    private final UserQuest userQuest;
    private final Boolean addQuest;


    public ModifyTrackedQuests(boolean addQuest, UserQuest userQuest) {
        this.userQuest = userQuest;
        this.addQuest = addQuest;
    }

    public ModifyTrackedQuests(FriendlyByteBuf buffer) {
        try {
            addQuest = buffer.readBoolean();
            userQuest = (UserQuest) GsonManager.getJsonClass(buffer.readUtf(), UserQuest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeBoolean(addQuest);
        buffer.writeUtf(GsonManager.gson.toJson(userQuest));
    }


    public static void handler(ModifyTrackedQuests msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.modifyTrackedQuests(msg.addQuest, msg.userQuest)));

        ctx.get().setPacketHandled(true);
    }
}

