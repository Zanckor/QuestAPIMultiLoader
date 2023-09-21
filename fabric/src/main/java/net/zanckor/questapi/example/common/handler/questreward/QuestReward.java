package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.server.ServerRequirement;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.zanckor.questapi.CommonMain.*;

@SuppressWarnings("ConstantConditions")
public class QuestReward extends AbstractReward {

    /**
     * Type of reward, gives player another quest (multistage) set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        String quest = serverQuest.getRewards().get(rewardIndex).getTag() + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;

            Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
            ServerQuest rewardServerQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks all quest requirements and return if player hasn't any requirement
            for (int requirementIndex = 0; requirementIndex < rewardServerQuest.getRequirements().size(); requirementIndex++) {
                ServerRequirement serverRequirement = serverQuest.getRequirements().get(requirementIndex);
                String requirementType = serverRequirement.getType() != null ? serverRequirement.getType() : "NONE";
                Enum<?> questRequirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

                AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                if (!requirement.handler(player, rewardServerQuest, requirementIndex)) return;
            }

            UserQuest userQuest = UserQuest.createQuest(rewardServerQuest, path);
            GsonManager.writeJson(path.toFile(), userQuest);

            if (userQuest.hasTimeLimit()) {
                Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
            }

            QuestDialogManager.registerQuestByID(serverQuest.getRewards().get(rewardIndex).getTag(), path);

            break;
        }
    }
}