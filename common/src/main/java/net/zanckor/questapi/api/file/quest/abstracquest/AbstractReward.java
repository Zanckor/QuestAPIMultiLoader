package net.zanckor.questapi.api.file.quest.abstracquest;


import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;

import java.io.IOException;

public abstract class AbstractReward {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player        The player
     * @param questTemplate ServerQuestBase with global quest data
     */

    public abstract void handler(ServerPlayer player, ServerQuest questTemplate, int rewardIndex) throws IOException;
}
