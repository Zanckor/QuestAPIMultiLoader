package net.zanckor.questapi;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.zanckor.questapi.api.screenmanager.AbstractQuestTracked;
import net.zanckor.questapi.api.screenmanager.ScreenRegistry;
import net.zanckor.questapi.example.ModExample;
import net.zanckor.questapi.mod.common.config.client.RendererConfig;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;
import net.zanckor.questapi.mod.common.network.NetworkHandler;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@Mod(MOD_ID)
public class ForgeQuestAPI {

    public ForgeQuestAPI() {
        CommonMain.init();

        LOG.info("Loading QuestAPI to Forge");
        new ModExample();
        MinecraftForge.EVENT_BUS.register(this);
        NetworkHandler.register();

        LOG.info("Registering config files");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ScreenConfig.SPEC, "questapi-screen.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RendererConfig.SPEC, "questapi-renderer.toml");
    }


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEventHandlerRegister {
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

        public static KeyMapping questMenu;

        public static KeyMapping registerKey(String name, int keycode) {
            LOG.info("Registering keys");

            return new KeyMapping("key." + MOD_ID + "." + name, keycode, "key.categories.QuestApi");
        }

        @SubscribeEvent
        public static void keyInit(RegisterKeyMappingsEvent e) {
            questMenu = registerKey("quest_menu", InputConstants.KEY_K);

            e.register(questMenu);
        }
    }
}