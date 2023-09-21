package net.zanckor.questapi.mod.common.network.packet.dialogoption;

import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.dialog.codec.NPCDialog;
import net.zanckor.questapi.api.screen.NpcType;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.mod.common.util.MCUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class DisplayDialog {

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


    public void encodeBuffer(FriendlyByteBuf buffer) {
        NPCDialog.QuestDialog dialog = dialogTemplate.getDialog().get(dialogID);

        encodeNpcType(buffer);
        buffer.writeUtf(identifier);

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
    }

    private void encodeNpcType(FriendlyByteBuf buf) {
        buf.writeEnum(npcType);

        switch (npcType) {
            case ITEM -> buf.writeItem(item.getDefaultInstance());
            case UUID -> buf.writeUUID(entityUUID);
            case RESOURCE_LOCATION -> buf.writeUtf(resourceLocation);
        }
    }

    public DisplayDialog(FriendlyByteBuf buffer) {
        decodeNpcType(buffer);
        identifier = buffer.readUtf();

        questDialog = buffer.readUtf();
        optionSize = buffer.readInt();

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
    }

    private void decodeNpcType(FriendlyByteBuf buf) {
        npcType = buf.readEnum(NpcType.class);

        switch (npcType) {
            case ITEM -> item = buf.readItem().getItem();
            case UUID -> entityUUID = buf.readUUID();
            case RESOURCE_LOCATION -> resourceLocation = buf.readUtf();
        }
    }

    public static void handler(DisplayDialog msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ClientHandler.displayDialog(msg.identifier, msg.dialogID, msg.questDialog, msg.optionSize, msg.optionIntegers, msg.optionStrings, msg.entityUUID, msg.resourceLocation, msg.item, msg.npcType)));

        ctx.get().setPacketHandled(true);
    }
}
