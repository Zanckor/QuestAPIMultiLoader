package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;

import java.io.IOException;

import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.INTERACT_SPECIFIC_ENTITY;


@EventSubscriber(side = Side.SERVER)
public class InteractSpecificEntityEvent {

    @SubscribeEvent(event = PlayerInteractEvent.PlayerInteractEntity.class)
    public static void interactWithNPC(Player player, Entity entity, InteractionHand interactionHand) throws IOException {
        if (interactionHand.equals(MAIN_HAND) &&
                Timer.canUseWithCooldown(player.getUUID(), "INTERACT_SPECIFIC_EVENT_COOLDOWN", 1)) {

            NetworkServerHandler.questHandler(INTERACT_SPECIFIC_ENTITY, (ServerPlayer) player, (LivingEntity) entity);
            Timer.updateCooldown(player.getUUID(), "INTERACT_SPECIFIC_EVENT_COOLDOWN", 1);
        }
    }
}