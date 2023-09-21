package net.zanckor.questapi.example.common.handler.questgoal;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.questhandler.ForgeAbstractGoal;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.INTERACT_SPECIFIC_ENTITY;

@SuppressWarnings("ConstantConditions, rawtypes")
public class InteractSpecificEntityGoal extends ForgeAbstractGoal {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        //Checks if interacted entity equals to target and if it is, checks if current progress is more than target amount
        if (questGoal.getCurrentAmount() >= questGoal.getAmount() || !(questGoal.getTarget().equals(EntityType.getKey(entity.getType()).toString())) || !checkEntityNBT(questGoal, entity))
            return;

        questGoal.incrementCurrentAmount(1);
        GsonManager.writeJson(file, userQuest);

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }

    @SuppressWarnings("all")
    private boolean checkEntityNBT(UserGoal questGoal, Entity entity) {
        Map<String, String> nbtTags = GsonManager.gson.fromJson(questGoal.getAdditionalClassData().toString(), new TypeToken<Map<String, String>>() {
        }.getType());
        CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(entity);
        AtomicBoolean nbtMatches = new AtomicBoolean(false);

        nbtTags.forEach((nbtTag, nbtValue) -> {
            nbtMatches.set(false);

            if (entityNBT == null || !entityNBT.get(nbtTag).getAsString().contains(nbtValue)) {
                return;
            }

            nbtMatches.set(true);
        });

        return nbtMatches.get();
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {

    }

    @Override
    public void updateData(ServerPlayer player, File file) {
    }

    @Override
    public Enum getGoalType() {
        return INTERACT_SPECIFIC_ENTITY;
    }
}