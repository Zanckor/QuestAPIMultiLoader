package net.zanckor.questapi.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.common.network.message.AbstractPacket;

public class SendQuestPacket {

    public static void NEAR(Player sender, AbstractPacket packet, double radius) {
        if (sender instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) sender;

            for (ServerPlayer nearPlayer : PlayerLookup.around(serverPlayer.serverLevel().getLevel(), sender.getPosition(0), radius)) {
                ServerPlayNetworking.send(nearPlayer, packet.getID(), packet.encode());
            }
        }
    }

    public static void LEVEL(Player sender, AbstractPacket packet) {
        sender.level().players().forEach(player -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            ServerPlayNetworking.send(serverPlayer, packet.getID(), packet.encode());
        });
    }

    public static void TO_CLIENT(AbstractPacket packet) {
        ClientPlayNetworking.send(packet.getID(), packet.encode());
    }

    public static void TO_SERVER(ServerPlayer player, AbstractPacket packet) {
        ServerPlayNetworking.send(player, packet.getID(), packet.encode());
    }
}
