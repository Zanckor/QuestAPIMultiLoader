package net.zanckor.questapi;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.api.registry.ScreenRegistry;
import net.zanckor.questapi.api.screen.AbstractQuestTracked;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientForgeQuestAPI {
    public static KeyMapping questMenu;

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        LOG.info("Registering overlay screens");

        e.registerAboveAll("quest_tracker", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
            Player player = Minecraft.getInstance().player;

            if (player != null && !player.isDeadOrDying()) {
                AbstractQuestTracked abstractQuestTracked = ScreenRegistry.getQuestTrackedScreen(ScreenConfig.QUEST_TRACKED_SCREEN.get());
                abstractQuestTracked.renderQuestTracked(guiGraphics, screenWidth, screenHeight);
            }
        });
    }

    @SubscribeEvent
    public static void keyInit(RegisterKeyMappingsEvent e) {
        questMenu = registerKey("quest_menu", InputConstants.KEY_K);

        e.register(questMenu);
    }

    public static KeyMapping registerKey(String name, int keycode) {
        LOG.info("Registering keys");

        return new KeyMapping("key." + MOD_ID + "." + name, keycode, "key.categories.QuestApi");
    }
}