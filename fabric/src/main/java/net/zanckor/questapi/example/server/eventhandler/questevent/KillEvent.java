package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.KILL;


@EventSubscriber(side = Side.SERVER)
public class KillEvent {
    @SubscribeEvent(event = ServerEntityCombatEvents.AfterKilledOtherEntity.class)
    public static void killQuest(ServerLevel level, Entity entity, LivingEntity killedEntity) throws IOException {
        if (!(entity instanceof ServerPlayer player)) return;

        NetworkServerHandler.questHandler(KILL, player, killedEntity);
    }
}