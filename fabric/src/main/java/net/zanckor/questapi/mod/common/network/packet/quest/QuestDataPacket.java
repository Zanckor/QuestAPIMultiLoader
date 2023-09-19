package net.zanckor.questapi.mod.common.network.packet.quest;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

import java.io.IOException;
import java.util.UUID;

@SuppressWarnings(value = "ConstantConditions, rawtypes, unused, unchecked")
public class QuestDataPacket extends AbstractPacket {


    /**
     * Packet to execute a quest handler from client to server
     * <p>
     * Be careful using this from client side, may cause exploits
     */

    Enum quest;
    UUID entity;

    public QuestDataPacket(Enum quest, Entity entity) {
        this.quest = quest;
        this.entity = entity.getUUID();
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();

        buffer.writeEnum(quest);
        buffer.writeUUID(entity);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.QUEST_DATA_PACKET;
    }


    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
        Enum quest = buffer.readEnum(Enum.class);
        UUID entityUUID = buffer.readUUID();
        LivingEntity entity = (LivingEntity) Util.getEntityByUUID((ServerLevel) player.level(), entityUUID);

        server.execute(() -> handler((ServerPlayer) player, quest, entity));
    }

    private static void handler(ServerPlayer player, Enum quest, LivingEntity entity) {
        try {
            NetworkServerHandler.questHandler(quest, player, entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
