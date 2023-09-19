package net.zanckor.questapi.mod.common.network.packet.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class ModifyTrackedQuests extends AbstractPacket {

    UserQuest userQuest;
    Boolean addQuest;

    public ModifyTrackedQuests(boolean addQuest, UserQuest userQuest) {
        this.userQuest = userQuest;
        this.addQuest = addQuest;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(addQuest);
        buffer.writeUtf(GsonManager.gson.toJson(userQuest));

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.MODIFY_TRACKED_QUESTS;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        boolean addQuest = false;
        UserQuest userQuest = null;

        try {
            addQuest = buffer.readBoolean();
            userQuest = (UserQuest) GsonManager.getJsonClass(buffer.readUtf(), UserQuest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserQuest finalUserQuest = userQuest;
        boolean finalAddQuest = addQuest;
        client.execute(() -> handler(finalAddQuest, finalUserQuest));
    }

    private static void handler(boolean addQuest, UserQuest userQuest) {
        NetworkClientHandler.modifyTrackedQuests(addQuest, userQuest);
    }
}
