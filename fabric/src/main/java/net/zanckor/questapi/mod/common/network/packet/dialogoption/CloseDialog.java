package net.zanckor.questapi.mod.common.network.packet.dialogoption;

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

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class CloseDialog extends AbstractPacket {

    public CloseDialog() {
    }

    @Override
    public FriendlyByteBuf encode() {
        return PacketByteBufs.create();
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.CLOSE_DIALOG;
    }


    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
        client.execute(CloseDialog::handler);
    }

    private static void handler() {
        NetworkClientHandler.closeDialog();
    }
}

