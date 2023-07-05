package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static net.zanckor.questapi.CommonMain.*;

public class QuestReward extends AbstractReward {

    /**
     * Type of reward, gives player another quest (multistage) set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @param rewardIndex
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
                Enum questRequirementEnum = EnumRegistry.getEnum(rewardServerQuest.getRequirements().get(requirementIndex).getType(), EnumRegistry.getQuestRequirement());
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