package net.zanckor.questapi.mod.common.network.packet.dialogoption;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;

public class AddQuest extends AbstractPacket {
    EnumDialogOption dialogOption;
    int optionID;

    public AddQuest(EnumDialogOption dialogOption, int optionID) {
        this.dialogOption = dialogOption;
        this.optionID = optionID;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeEnum(dialogOption);
        buffer.writeInt(optionID);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.ADD_QUEST;
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
        EnumDialogOption dialogOption = buffer.readEnum(EnumDialogOption.class);
        int optionID = buffer.readInt();

        server.execute(() -> handler(player, dialogOption, optionID));
    }

    private static void handler(Player player, EnumDialogOption dialogOption, int optionID) {
        NetworkServerHandler.addQuest(player, dialogOption, optionID);
    }
}