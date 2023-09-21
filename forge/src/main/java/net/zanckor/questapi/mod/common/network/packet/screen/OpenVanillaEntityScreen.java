package net.zanckor.questapi.mod.common.network.packet.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.zanckor.questapi.util.Util;

import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class OpenVanillaEntityScreen {

    UUID entityUUID;


    public OpenVanillaEntityScreen(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public OpenVanillaEntityScreen(FriendlyByteBuf buffer) {
        entityUUID = buffer.readUUID();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUUID(entityUUID);
    }


    public static void handler(OpenVanillaEntityScreen msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            Entity entity = Util.getEntityByUUID(ctx.get().getSender().serverLevel(), msg.entityUUID);

            player.setShiftKeyDown(true);
            player.interactOn(entity, InteractionHand.MAIN_HAND);
        });

        ctx.get().setPacketHandled(true);
    }
}