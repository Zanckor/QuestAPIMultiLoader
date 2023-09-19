package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.XP;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XPEvent {
    @SubscribeEvent
    public static void xpQuest(PlayerXpEvent e) throws IOException {
        if (!(e.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) return;

        ServerHandler.questHandler(XP, player, e.getEntity());
    }
}