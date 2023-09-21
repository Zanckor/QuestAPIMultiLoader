package net.zanckor.questapi.mod.common.questhandler;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Util;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.zanckor.questapi.mod.common.network.packet.quest.ToastPacket;
import net.zanckor.questapi.mod.common.network.packet.screen.UpdateQuestTracked;

import java.io.File;
import java.io.IOException;

import static net.zanckor.questapi.CommonMain.serverQuests;


@SuppressWarnings("ConstantConditions, rawtypes")
public abstract class FabricAbstractGoal extends AbstractGoal {


    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum<?> questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));

        if (Util.isQuestCompleted(userQuest)) completeQuest(player, userQuest, file);
    }

    protected void completeQuest(ServerPlayer player, UserQuest userQuest, File file) throws IOException {
        if (userQuest == null) return;

        //Update file and load it again
        callUpdate(userQuest, player, file);
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        //Checks if quest is completed and then rewrite file to quest completed.
        //Also gives rewards and send a notification to player
        if (Util.isQuestCompleted(userQuest)) {
            userQuest.setCompleted(true);
            GsonManager.writeJson(file, userQuest);

            callEnhancedReward(userQuest, player, file);

            giveReward(player, file, userQuest);
            SendQuestPacket.TO_CLIENT(player, new ToastPacket(userQuest.getTitle()));
        }

        //Update list of active quests on client side
        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
    }

    public abstract void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) throws IOException;


    /**
     * Checks each file until get a file corresponding with questID, read it and gives rewards
     */
    protected void giveReward(ServerPlayer player, File file, UserQuest userQuest) throws IOException {
        if (!(userQuest.isCompleted())) return;
        String questID = userQuest.getId() + ".json";

        for (File serverFile : serverQuests.toFile().listFiles()) {
            if (!(serverFile.getName().equals(questID))) continue;
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(serverFile, ServerQuest.class);

            if (serverQuest.getRewards() == null) return;

            for (int rewardIndex = 0; rewardIndex < serverQuest.getRewards().size(); rewardIndex++) {
                Enum rewardEnum = EnumRegistry.getEnum(serverQuest.getRewards().get(rewardIndex).getType(), EnumRegistry.getQuestReward());
                AbstractReward reward = QuestTemplateRegistry.getQuestReward(rewardEnum);

                reward.handler(player, serverQuest, rewardIndex);
            }

            Util.moveFileToCompletedFolder(userQuest, player, file);

            return;
        }
    }


    protected void callUpdate(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        for (UserGoal goal : userQuest.getQuestGoals()) {
            for (AbstractGoal abstractGoal : QuestTemplateRegistry.getAllGoals().values()) {
                if (goal.getType().equals(abstractGoal.getGoalType().toString())) {
                    abstractGoal.updateData(player, file);
                }
            }
        }
    }

    protected void callEnhancedReward(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        for (UserGoal goal : userQuest.getQuestGoals()) {
            for (AbstractGoal abstractGoal : QuestTemplateRegistry.getAllGoals().values()) {
                if (goal.getType().equals(abstractGoal.getGoalType().toString())) {
                    abstractGoal.enhancedCompleteQuest(player, file, goal);
                }
            }
        }
    }
}
