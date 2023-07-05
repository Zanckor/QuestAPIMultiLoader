package net.zanckor.questapi.example.common.handler.questgoal;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.mod.common.questhandler.ForgeAbstractGoal;

import java.io.File;
import java.io.IOException;

import static net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumquest.EnumGoalType.XP;

public class XpGoal extends ForgeAbstractGoal {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        if (questGoal.getCurrentAmount() == null) return;

        int currentAmount = player.experienceLevel >= questGoal.getAmount() ? questGoal.getAmount() : player.experienceLevel;
        questGoal.setCurrentAmount(currentAmount);
        GsonManager.writeJson(file, userQuest);

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {
        player.setExperienceLevels(player.experienceLevel - userGoal.getAmount());
    }

    @Override
    public void updateData(ServerPlayer player, File file) throws IOException {
    }

    @Override
    public Enum getGoalType() {
        return XP;
    }
}
