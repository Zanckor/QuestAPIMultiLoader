package net.zanckor.questapi.common.network.message.dialogoption;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.common.network.NetworkHandler;
import net.zanckor.questapi.common.network.message.AbstractPacket;

/*
public class AddQuest extends AbstractPacket {
    EnumDialogOption optionType;
    int optionID;

    public AddQuest(EnumDialogOption optionType, int optionID) {
        this.optionType = optionType;
        this.optionID = optionID;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeEnum(optionType);
        buffer.writeInt(optionID);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.ADD_QUEST;
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender){
        EnumDialogOption dialogOption = buffer.readEnum(EnumDialogOption.class);
        int optionID = buffer.readInt();

        server.execute(() -> handler(server, handler, sender, dialogOption, optionID));
    }

    private static void handler(MinecraftServer server, ServerPacketListener handler, PacketSender sender, EnumDialogOption dialogOption, int optionID){
        System.out.println("A");
    }
}
 */
