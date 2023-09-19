package net.zanckor.questapi.mod.common.network.packet.npcmarker;

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

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static net.zanckor.questapi.api.datamanager.QuestDialogManager.dialogPerCompoundTag;
import static net.zanckor.questapi.api.datamanager.QuestDialogManager.dialogPerEntityType;

@Environment(EnvType.CLIENT)
public class ValidNPCMarker extends AbstractPacket {
    List<String> entityTypeList;
    Map<String, String> entityTagMap;

    public ValidNPCMarker() {
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        try {
            buffer.writeUtf(dialogPerEntityType.keySet().toString());

            //Convert String - File to String - String
            HashMap<String, String> entityTagMap = new HashMap<>();

            for (String key : dialogPerCompoundTag.keySet()) {
                String fileContent = Files.readString(dialogPerCompoundTag.get(key).toPath());

                entityTagMap.put(key, fileContent);
            }

            buffer.writeMap(entityTagMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.VALID_NPC_MARKER;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        String entityStringList = buffer.readUtf();

        List<String> entityTypeList = new ArrayList<>(Arrays.asList(entityStringList.substring(1, entityStringList.length() - 1).split(",")));
        Map<String, String> entityTagMap = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);

        client.execute(() -> handler(entityTypeList, entityTagMap));
    }

    private static void handler(List<String> entityTypeList, Map<String, String> entityTagMap) {
        NetworkClientHandler.setAvailableEntityTypeForQuest(entityTypeList, entityTagMap);
    }
}
