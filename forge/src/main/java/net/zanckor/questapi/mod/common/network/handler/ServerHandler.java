package net.zanckor.questapi.mod.common.network.handler;

import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.screenmanager.NpcType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.commonutil.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.playerData;

public class ServerHandler {

    public static void addQuest(Player player, Enum optionType, int optionID) {
        String dialogGlobalID = QuestDialogManager.currentGlobalDialog.get(player);

        Path path = QuestDialogManager.getDialogLocation(dialogGlobalID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);

        try {
            NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);

            dialogTemplate.handler(player, dialog, optionID, (Entity) null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void questHandler(Enum questType, ServerPlayer player, LivingEntity entity) throws IOException {
        AbstractGoal quest = QuestTemplateRegistry.getQuestTemplate(questType);
        List<Path> questTypeLocation = QuestDialogManager.getQuestTypeLocation(questType);

        if (quest == null || questTypeLocation == null) return;

        for (int i = 0; i < questTypeLocation.size(); i++) {
            File file = questTypeLocation.get(i).toAbsolutePath().toFile();
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
            if (userQuest == null || userQuest.isCompleted()) continue;

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                if (userQuest == null) return;

                UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

                if (questGoal.getType().equals(questType.toString())) {
                    quest.handler(player, entity, GsonManager.gson, file, userQuest, indexGoals, questType);
                }
            }
        }
    }

    public static void requestDialog(ServerPlayer player, int optionID, Enum optionType, UUID entityUUID, String resourceLocation, Item item, NpcType npcType) {
        String globalDialogID = QuestDialogManager.currentGlobalDialog.get(player);

        Path path = QuestDialogManager.getDialogLocation(globalDialogID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);

        try {
            NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);

            switch (npcType) {
                case ITEM -> dialogTemplate.handler(player, dialog, optionID, item);
                case UUID ->
                        dialogTemplate.handler(player, dialog, optionID, Util.getEntityByUUID(player.serverLevel().getLevel(), entityUUID));
                case RESOURCE_LOCATION -> dialogTemplate.handler(player, dialog, optionID, resourceLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void questTimer(ServerLevel level) {

        for (Player player : level.players()) {
            Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

            for (File file : userFolder.toFile().listFiles()) {
                try {
                    UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                    if (userQuest.isCompleted() || !userQuest.hasTimeLimit() || !Timer.canUseWithCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds()))
                        return;

                    userQuest.setCompleted(true);
                    GsonManager.writeJson(file, userQuest);

                } catch (IOException exception) {
                    LOG.error(exception.getMessage());
                }
            }
        }
    }
}