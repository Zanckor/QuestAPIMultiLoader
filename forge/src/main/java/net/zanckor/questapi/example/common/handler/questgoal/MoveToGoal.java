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

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.MOVE_TO;

@SuppressWarnings("rawtypes")
public class MoveToGoal extends ForgeAbstractGoal {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {

        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        if (!(questGoal.getType().equals(MOVE_TO.toString()))) return;
        questGoal.setCurrentAmount(1);
        GsonManager.writeJson(file, userQuest);

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {

    }

    @Override
    public void updateData(ServerPlayer player, File file) {
    }

    @Override
    public Enum getGoalType() {
        return MOVE_TO;
    }
}