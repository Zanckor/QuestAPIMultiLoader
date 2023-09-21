package net.zanckor.questapi.example;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
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


@EventSubscriber(side = Side.SERVER)
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

        Arrays.stream(EnumGoalType.values()).forEach(QuestTemplateRegistry::registerQuest);
        Arrays.stream(EnumQuestReward.values()).forEach(QuestTemplateRegistry::registerReward);
        Arrays.stream(EnumQuestRequirement.values()).forEach(QuestTemplateRegistry::registerQuestRequirement);
        Arrays.stream(EnumDialogReq.values()).forEach(QuestTemplateRegistry::registerDialogRequirement);
        Arrays.stream(EnumDialogOption.values()).forEach(QuestTemplateRegistry::registerDialogOption);
    }


    @SubscribeEvent(event = ServerLifecycleEvents.ServerStarting.class)
    public static void registerTemplates(MinecraftServer server) throws IOException {
        LOG.info("Register Template files");

        LoadQuest.registerQuest(server, MOD_ID);
        LoadDialog.registerDialog(server, MOD_ID);
        LoadDialogList.registerNPCDialogList(server, MOD_ID);
        LoadTagDialogList.registerNPCTagDialogList(server, MOD_ID);


        //Do not mind bout this, is background logic for data-packs.
        LoadQuest.registerDatapackQuest(server);
        LoadDialog.registerDatapackDialog(server);
        LoadDialogList.registerDatapackDialogList(server);
        LoadTagDialogList.registerDatapackTagDialogList(server);
    }
}
