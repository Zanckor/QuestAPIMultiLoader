package net.zanckor.questapi.mod.common.network.packet.screen;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.util.Util;
import net.zanckor.questapi.mod.common.network.NetworkHandler;
import net.zanckor.questapi.mod.common.network.packet.AbstractPacket;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class OpenVanillaEntityScreen extends AbstractPacket {
    UUID entityUUID;

    public OpenVanillaEntityScreen(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeUUID(entityUUID);

        return buffer;
    }

    @Override
    public ResourceLocation getID() {
        return NetworkHandler.OPEN_VANILLA_ENTITY_SCREEN;
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
        UUID entityUUID = buffer.readUUID();
        Entity entity = Util.getEntityByUUID((ServerLevel) player.level(), entityUUID);

        server.execute(() -> handler(player, entity));
    }

    private static void handler(Player player, Entity entity) {
        player.setShiftKeyDown(true);
        player.interactOn(entity, InteractionHand.MAIN_HAND);
    }
}
