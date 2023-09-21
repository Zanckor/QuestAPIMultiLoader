package net.zanckor.questapi.mod.common.network.handler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.screen.NpcType;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.mod.common.util.MCUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.playerData;

@SuppressWarnings("ConstantConditions, rawtypes")
public class NetworkServerHandler {
    public static void addQuest(Player player, Enum optionType, int optionID) {
        String dialogGlobalID = QuestDialogManager.currentGlobalDialog.get(player);

        Path path = QuestDialogManager.getConversationPathLocation(dialogGlobalID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);

        try {
            Conversation dialog = (Conversation) GsonManager.getJsonClass(dialogFile, Conversation.class);

            dialogTemplate.handler(player, dialog, optionID, (Entity) null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void questHandler(Enum questType, ServerPlayer player, LivingEntity entity) throws IOException {
        AbstractGoal quest = QuestTemplateRegistry.getQuestTemplate(questType);
        List<Path> questTypeLocation = QuestDialogManager.getQuestTypePathLocation(questType);

        if (quest == null || questTypeLocation == null) return;

        for (Path path : questTypeLocation) {
            File file = path.toAbsolutePath().toFile();
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


    public static void requestDialog(ServerPlayer player, int optionID, Enum optionType, UUID entityUUID, Item item, NpcType npcType) {
        String globalDialogID = QuestDialogManager.currentGlobalDialog.get(player);

        Path path = QuestDialogManager.getConversationPathLocation(globalDialogID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);
        try {
            Conversation dialog = (Conversation) GsonManager.getJsonClass(dialogFile, Conversation.class);

            switch (npcType) {
                case ITEM -> dialogTemplate.handler(player, dialog, optionID, item);
                case RESOURCE_LOCATION, UUID ->
                        dialogTemplate.handler(player, dialog, optionID, MCUtil.getEntityByUUID((ServerLevel) player.level(), entityUUID));
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
