package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerXPEvent;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.XP;

@EventSubscriber
public class XPEvent {
    @SubscribeEvent(event = PlayerXPEvent.PlayerChangeXP.class)
    public static void xpQuest(ServerPlayer player) throws IOException {
        System.out.println("XPEvent.xpQuest");

        NetworkServerHandler.questHandler(XP, player, player);
    }
}