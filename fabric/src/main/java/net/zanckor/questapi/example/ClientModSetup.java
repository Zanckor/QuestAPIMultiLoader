package net.zanckor.questapi.example;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.screenmanager.AbstractQuestTracked;
import net.zanckor.questapi.api.screenmanager.ScreenRegistry;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.example.client.screen.dialog.DialogScreen;
import net.zanckor.questapi.example.client.screen.dialog.MinimalistDialogScreen;
import net.zanckor.questapi.example.client.screen.hud.RenderQuestTracker;
import net.zanckor.questapi.example.client.screen.questlog.QuestLog;
import net.zanckor.questapi.example.common.entity.client.NPCRenderer;
import net.zanckor.questapi.example.core.registry.NpcRegistry;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType;

import java.util.Arrays;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.common.config.client.ScreenConfig.QUEST_TRACKED_SCREEN;

@Environment(EnvType.CLIENT)
@EventSubscriber(side = Side.CLIENT)
public class ClientModSetup {
    public ClientModSetup() {
        EntityRendererRegistry.INSTANCE.register(NpcRegistry.NPC_ENTITY, NPCRenderer::new);
        Arrays.stream(EnumGoalType.EnumTargetType.values()).forEach(QuestTemplateRegistry::registerTargetType);

        registerScreen();
    }

    @SubscribeEvent(event = HudRenderCallback.class)
    public static void registerOverlay(GuiGraphics graphics, float tickDelta) {
        Player player = Minecraft.getInstance().player;
        Window window = Minecraft.getInstance().getWindow();

        if (player != null && !player.isDeadOrDying()) {
            AbstractQuestTracked abstractQuestTracked = ScreenRegistry.getQuestTrackedScreen(QUEST_TRACKED_SCREEN);
            abstractQuestTracked.renderQuestTracked(graphics, window.getWidth() / 2, window.getHeight() / 2);
        }
    }

    private static void registerScreen() {
        ScreenRegistry.registerDialogScreen(MOD_ID, new DialogScreen(Component.literal("dialog_screen")));
        ScreenRegistry.registerDialogScreen("minimalist_" + MOD_ID, new MinimalistDialogScreen(Component.literal("minimalist_dialog_screen")));
        ScreenRegistry.registerQuestTrackedScreen(MOD_ID, new RenderQuestTracker());
        ScreenRegistry.registerQuestLogScreen(MOD_ID, new QuestLog(Component.literal("quest_log_screen")));
    }
}
