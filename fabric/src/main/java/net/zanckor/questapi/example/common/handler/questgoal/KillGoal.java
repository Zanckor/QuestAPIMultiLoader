package net.zanckor.questapi.example.common.handler.questgoal;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.questhandler.FabricAbstractGoal;

import java.io.File;
import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.KILL;

@SuppressWarnings("ConstantConditions, rawtypes")
public class KillGoal extends FabricAbstractGoal {
    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        //Checks if killed entity equals to target and if it is, checks if current progress is more than target amount
        if (questGoal.getCurrentAmount() >= questGoal.getAmount() || !(questGoal.getTarget().equals(EntityType.getKey(entity.getType()).toString())))
            return;

        questGoal.incrementCurrentAmount(1);
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
        return KILL;
    }
}
