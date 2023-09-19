package net.zanckor.questapi.example.client.screen.hud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.commonutil.Timer;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.common.network.handler.ClientHandler.trackedQuestList;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuestTrackedTimer {
    @SubscribeEvent
    public static void tickEvent(TickEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || trackedQuestList.isEmpty() || !(e.phase.equals(TickEvent.Phase.START))) {
            return;
        }

        trackedQuestList.forEach(quest -> {
            if (mc.player.tickCount % 20 == 0 && quest.hasTimeLimit() && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) && quest.getTimeLimitInSeconds() > 0) {
                quest.setTimeLimitInSeconds((int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) / 1000);
            }
        });
    }
}
