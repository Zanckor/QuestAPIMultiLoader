package net.zanckor.questapi.mod.common.network.packet.dialogoption;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.dialog.codec.NPCDialog;
import net.zanckor.questapi.api.screen.NpcType;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;
import net.zanckor.questapi.mod.common.util.MCUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class DisplayDialog extends AbstractPacket {

    Conversation dialogTemplate;

    String identifier;
    int dialogID;
    int optionSize;
    String questDialog;
    HashMap<Integer, List<String>> optionStrings = new HashMap<>();
    HashMap<Integer, List<Integer>> optionIntegers = new HashMap<>();

    UUID entityUUID;
    String resourceLocation;
    Item item;

    NpcType npcType;


    private void displayDialog(Conversation conversation, String identifier, int dialogID, Player player) throws IOException {
        this.dialogTemplate = conversation;
        this.dialogID = dialogID;
        this.identifier = identifier != null ? identifier : "questapi";

        QuestDialogManager.currentDialog.put(player, dialogID);
        MCUtil.writeDialogRead(player, conversation.getConversationID(), dialogID);
    }

    public DisplayDialog(Conversation dialogTemplate, String identifier, int dialogID, Player player, Entity entity) throws IOException {
        displayDialog(dialogTemplate, identifier, dialogID, player);
        this.entityUUID = entity == null ? player.getUUID() : entity.getUUID();
        this.npcType = NpcType.UUID;
    }

    public DisplayDialog(Conversation dialogTemplate, String identifier, int dialogID, Player player, String resourceLocation) throws IOException {
        displayDialog(dialogTemplate, identifier, dialogID, player);
        this.resourceLocation = resourceLocation;
        this.npcType = NpcType.RESOURCE_LOCATION;
    }

    public DisplayDialog(Conversation dialogTemplate, String identifier, int dialogID, Player player, Item item) throws IOException {
        displayDialog(dialogTemplate, identifier, dialogID, player);
        this.item = item;
        this.npcType = NpcType.ITEM;
    }


    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();

        NPCDialog.QuestDialog dialog = dialogTemplate.getDialog().get(dialogID);

        encodeNpcType(buffer);
        buffer.writeUtf(identifier);
        buffer.writeInt(dialogID);

        buffer.writeUtf(dialog.getDialogText());
        buffer.writeInt(dialog.getOptions().size());

        for (NPCDialog.DialogOption option : dialog.getOptions()) {
            String optionText = option.getText() == null ? "" : option.getText();
            String optionQuestID = option.getQuest_id() == null ? "" : option.getQuest_id();
            String optionGlobalID = option.getGlobal_id() == null ? "" : option.getGlobal_id();

            buffer.writeUtf(optionText);
            buffer.writeUtf(option.getType());
            buffer.writeUtf(optionQuestID);
            buffer.writeUtf(optionGlobalID);
            buffer.writeInt(option.getDialog());
        }

        return buffer;
    }

    private void encodeNpcType(FriendlyByteBuf buf) {
        buf.writeEnum(npcType);

        switch (npcType) {
            case ITEM -> buf.writeItem(item.getDefaultInstance());
            case UUID -> buf.writeUUID(entityUUID);
            case RESOURCE_LOCATION -> buf.writeUtf(resourceLocation);
        }
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.DISPLAY_DIALOG;
    }


    @SuppressWarnings("all")
    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        NpcType npcType = buffer.readEnum(NpcType.class);
        Item item = null;
        UUID entityUUID = null;
        String resourceLocation = null;

        String identifier = buffer.readUtf();
        int dialogID = buffer.readInt();
        String questDialog = buffer.readUtf();
        int optionSize = buffer.readInt();

        HashMap<Integer, List<String>> optionStrings = new HashMap<>();
        HashMap<Integer, List<Integer>> optionIntegers = new HashMap<>();


        for (int optionSizeIndex = 0; optionSizeIndex < optionSize; optionSizeIndex++) {
            List<String> optionStringData = new ArrayList<>();
            List<Integer> optionIntegerData = new ArrayList<>();

            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());


            optionIntegerData.add(buffer.readInt());

            optionStrings.put(optionSizeIndex, optionStringData);
            optionIntegers.put(optionSizeIndex, optionIntegerData);
        }

        switch (npcType) {
            case ITEM -> item = buffer.readItem().getItem();
            case UUID -> entityUUID = buffer.readUUID();
            case RESOURCE_LOCATION -> resourceLocation = buffer.readUtf();
        }

        UUID finalEntityUUID = entityUUID;
        String finalResourceLocation = resourceLocation;
        Item finalItem = item;
        client.execute(() -> handler(identifier, dialogID, questDialog, optionSize, optionIntegers, optionStrings,
                finalEntityUUID, finalResourceLocation, finalItem, npcType));
    }

    private static void handler(String identifier, int dialogID, String questDialog, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID entityUUID, String resourceLocation, Item item, NpcType npcType) {
        NetworkClientHandler.displayDialog(identifier, dialogID, questDialog, optionSize, optionIntegers,
                optionStrings, entityUUID, resourceLocation, item, npcType);
    }
}
