package net.zanckor.questapi.example;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registry.ScreenRegistry;
import net.zanckor.questapi.example.client.screen.dialog.DialogScreen;
import net.zanckor.questapi.example.client.screen.dialog.MinimalistDialogScreen;
import net.zanckor.questapi.example.client.screen.hud.RenderQuestTracker;
import net.zanckor.questapi.example.client.screen.questlog.QuestLog;
import net.zanckor.questapi.example.core.registry.NpcRegistry;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.LoadDialog;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.LoadDialogList;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.LoadQuest;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.LoadTagDialogList;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestRequirement;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.IOException;
import java.util.Arrays;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModExample {

    /**
     * You can create your own EnumClass to add your templates:
     * <p><p>
     * <p>
     * DialogOption Needs to extend {@link AbstractDialogOption} <p>
     * Goal Needs to extend {@link AbstractGoal} <p>
     * Reward Needs to extend {@link AbstractReward} <p>
     * QuestRequirement Needs to extend {@link AbstractQuestRequirement} <p>
     * DialogRequirement Needs to extend {@link AbstractDialogRequirement} <p>
     * TargetType Needs to extend {@link AbstractTargetType}
     */


    public ModExample() {
        LOG.info("Creating Example code");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NpcRegistry.register(modEventBus);

        Arrays.stream(EnumGoalType.values()).forEach(QuestTemplateRegistry::registerQuest);
        Arrays.stream(EnumQuestReward.values()).forEach(QuestTemplateRegistry::registerReward);
        Arrays.stream(EnumQuestRequirement.values()).forEach(QuestTemplateRegistry::registerQuestRequirement);
        Arrays.stream(EnumDialogReq.values()).forEach(QuestTemplateRegistry::registerDialogRequirement);
        Arrays.stream(EnumDialogOption.values()).forEach(QuestTemplateRegistry::registerDialogOption);
    }


    @SubscribeEvent
    public static void registerTemplates(ServerAboutToStartEvent e) throws IOException {
        LOG.info("Register Template files");

        LoadQuest.registerQuest(e.getServer(), MOD_ID);
        LoadDialog.registerDialog(e.getServer(), MOD_ID);
        LoadDialogList.registerNPCDialogList(e.getServer(), MOD_ID);
        LoadTagDialogList.registerNPCTagDialogList(e.getServer(), MOD_ID);


        //Do not mind bout this, is background logic for data-packs.
        LoadQuest.registerDatapackQuest(e.getServer());
        LoadDialog.registerDatapackDialog(e.getServer());
        LoadDialogList.registerDatapackDialogList(e.getServer());
        LoadTagDialogList.registerDatapackTagDialogList(e.getServer());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModExample {

        @SubscribeEvent
        public static void registerTargetTypeEnum(FMLClientSetupEvent e) {
            Arrays.stream(EnumGoalType.EnumTargetType.values()).forEach(QuestTemplateRegistry::registerTargetType);
        }


        /**
         * registerScreen adds specified classes to cache to load X or Y screen depending on your identifier and config file
         */

        @SubscribeEvent
        public static void registerScreen(FMLClientSetupEvent e) {
            ScreenRegistry.registerDialogScreen(MOD_ID, new DialogScreen(Component.literal("dialog_screen")));
            ScreenRegistry.registerDialogScreen("minimalist_" + MOD_ID, new MinimalistDialogScreen(Component.literal("minimalist_dialog_screen")));
            ScreenRegistry.registerQuestTrackedScreen(MOD_ID, new RenderQuestTracker());
            ScreenRegistry.registerQuestLogScreen(MOD_ID, new QuestLog(Component.literal("quest_log_screen")));
        }
    }
}