package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInventoryEvent;
import net.zanckor.questapi.mod.common.network.handler.NetworkServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT_WITH_NBT;

@EventSubscriber(side = Side.SERVER)
public class CollectEvent {

    @SubscribeEvent(event = PlayerInventoryEvent.ChangeInventory.class)
    public static void changeInventory(ServerPlayer player) throws IOException {
        System.out.println("CollectEvent.changeInventory");

        runGoalHandler(player);
    }

    public static void runGoalHandler(ServerPlayer player) throws IOException {
        NetworkServerHandler.questHandler(COLLECT, player, null);
        NetworkServerHandler.questHandler(COLLECT_WITH_NBT, player, null);
    }
}
