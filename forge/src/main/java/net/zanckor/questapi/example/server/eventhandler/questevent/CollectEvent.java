package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT_WITH_NBT;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CollectEvent {

    @SubscribeEvent
    public static void CollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level().isClientSide) return;

        runGoalHandler((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void CollectCraftQuest(PlayerEvent.ItemCraftedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level().isClientSide) return;

        runGoalHandler((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void CollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level().isClientSide) return;

        runGoalHandler((ServerPlayer) e.getEntity());
    }

    public static void runGoalHandler(ServerPlayer player) throws IOException {
        ServerHandler.questHandler(COLLECT, player, null);
        ServerHandler.questHandler(COLLECT_WITH_NBT, player, null);
    }
}
