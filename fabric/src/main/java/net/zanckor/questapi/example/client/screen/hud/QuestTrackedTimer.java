package net.zanckor.questapi.example.client.screen.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;

import static net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler.trackedQuestList;

@SuppressWarnings("ConstantConditions")
@EventSubscriber
public class QuestTrackedTimer {
    @SubscribeEvent(event = ClientTickEvents.EndTick.class)
    public static void tickEvent(Minecraft mc) {

        trackedQuestList.forEach(quest -> {
            if (mc.player.tickCount % 20 == 0 && quest.hasTimeLimit() && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) && quest.getTimeLimitInSeconds() > 0) {
                quest.setTimeLimitInSeconds((int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) / 1000);
            }
        });
    }
}
