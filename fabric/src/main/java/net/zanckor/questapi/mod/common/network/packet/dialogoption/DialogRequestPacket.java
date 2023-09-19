package net.zanckor.questapi.mod.common.network.packet.dialogoption;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.screenmanager.NpcType;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;

import java.util.UUID;

public class DialogRequestPacket extends AbstractPacket {
    EnumDialogOption optionType;
    int optionID;
    UUID entityUUID;
    Item item;
    NpcType npcType;

    public DialogRequestPacket(EnumDialogOption optionType, int optionID, Entity npc, Item item, NpcType npcType) {
        this.optionType = optionType;
        this.optionID = optionID;
        this.entityUUID = npc.getUUID();
        this.item = item;
        this.npcType = npcType;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();

        buffer.writeEnum(optionType);
        buffer.writeInt(optionID);
        encodeNpcType(buffer);

        return buffer;
    }


    private void encodeNpcType(FriendlyByteBuf buf) {
        buf.writeEnum(npcType);

        switch (npcType) {
            case ITEM -> buf.writeItem(item.getDefaultInstance());
            case UUID, RESOURCE_LOCATION -> buf.writeUUID(entityUUID);
        }
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.DIALOG_REQUEST_PACKET;
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
        EnumDialogOption optionType = buffer.readEnum(EnumDialogOption.class);
        int optionID = buffer.readInt();
        NpcType npcType = buffer.readEnum(NpcType.class);
        Item item = null;
        UUID entityUUID = null;

        switch (npcType) {
            case ITEM -> item = buffer.readItem().getItem();
            case UUID, RESOURCE_LOCATION -> entityUUID = buffer.readUUID();
        }

        UUID finalEntityUUID = entityUUID;
        Item finalItem = item;
        server.execute(() -> handler((ServerPlayer) player, optionID, optionType, finalEntityUUID, finalItem, npcType));
    }


    public static void handler(ServerPlayer player, int optionID, EnumDialogOption optionType, UUID entityUUID, Item item, NpcType npcType) {
        NetworkServerHandler.requestDialog(player, optionID, optionType, entityUUID, item, npcType);
    }
}