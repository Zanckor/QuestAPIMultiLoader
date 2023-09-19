package net.zanckor.questapi.mod.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

@SuppressWarnings("unused")
public class SendQuestPacket {

    public static void NEAR(Player sender, AbstractPacket packet, double radius) {
        if (sender instanceof ServerPlayer serverPlayer) {
            for (ServerPlayer nearPlayer : PlayerLookup.around(serverPlayer.serverLevel().getLevel(), sender.getPosition(0), radius)) {
                ServerPlayNetworking.send(nearPlayer, packet.getID(), packet.encode());
            }
        }
    }

    public static void LEVEL(Player sender, AbstractPacket packet) {
        sender.level().players().forEach(player -> {
            ServerPlayNetworking.send((ServerPlayer) player, packet.getID(), packet.encode());
        });
    }

    public static void TO_CLIENT(Player player, AbstractPacket packet) {
        ServerPlayNetworking.send((ServerPlayer) player, packet.getID(), packet.encode());
    }

    public static void TO_SERVER(AbstractPacket packet) {
        ClientPlayNetworking.send(packet.getID(), packet.encode());
    }
}
