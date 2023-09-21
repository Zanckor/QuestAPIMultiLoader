package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.IOException;

public class XpReward extends AbstractReward {

    /**
     * Type of reward, gives player X quantity of xp set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        EnumQuestReward type = EnumQuestReward.valueOf(serverQuest.getRewards().get(rewardIndex).getTag());
        int quantity = serverQuest.getRewards().get(rewardIndex).getAmount();

        switch (type) {
            case LEVEL -> player.giveExperienceLevels(quantity);
            case POINTS -> player.giveExperiencePoints(quantity);
        }
    }
}