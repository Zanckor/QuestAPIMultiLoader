package net.zanckor.questapi.example.event.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumquest.EnumGoalType.KILL;


@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillEvent {
    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) throws IOException {
        if (!(e.getSource().getEntity() instanceof ServerPlayer player) || player.level().isClientSide) return;

        ServerHandler.questHandler(KILL, player, e.getEntity());
    }
}