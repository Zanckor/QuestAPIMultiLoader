package net.zanckor.questapi.mod.common.network.packet.quest;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

public class TimerPacket extends AbstractPacket {
    public TimerPacket() {
    }

    @Override
    public FriendlyByteBuf encode() {
        return PacketByteBufs.create();
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.TIMER_PACKET;
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
        server.execute(() -> handler((ServerPlayer) player));
    }

    private static void handler(ServerPlayer player) {
        NetworkServerHandler.questTimer(player.serverLevel());
    }
}
