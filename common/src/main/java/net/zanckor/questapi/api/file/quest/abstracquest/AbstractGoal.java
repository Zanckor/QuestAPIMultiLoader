package net.zanckor.questapi.api.file.quest.abstracquest;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Util;

import java.io.File;
import java.io.IOException;

import static net.zanckor.questapi.CommonMain.serverQuests;

@SuppressWarnings({"unused", "ConstantConditions"})
public abstract class AbstractGoal {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player The player
     * @throws IOException Exception fired when server cannot read json file
     */

    public abstract void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum<?> questType) throws IOException;

    protected abstract void completeQuest(ServerPlayer player, UserQuest userQuest, File file) throws IOException;

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
                Enum<?> rewardEnum = EnumRegistry.getEnum(serverQuest.getRewards().get(rewardIndex).getType(), EnumRegistry.getQuestReward());
                AbstractReward reward = QuestTemplateRegistry.getQuestReward(rewardEnum);

                reward.handler(player, serverQuest, rewardIndex);
            }

            Util.moveFileToCompletedFolder(userQuest, player, file);

            return;
        }
    }

    /**
     * Each goal updates his data and check if quest is completed ot run complete method
     */
    public abstract void updateData(ServerPlayer player, File file) throws IOException;


    /**
     * This method returns what type of goal is your current goal class designed for. See CollectGoal.jar for an example.
     */
    public abstract Enum<?> getGoalType();
}
