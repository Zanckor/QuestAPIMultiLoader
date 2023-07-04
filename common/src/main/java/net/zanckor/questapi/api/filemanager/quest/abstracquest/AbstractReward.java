package net.zanckor.questapi.api.filemanager.quest.abstracquest;


import net.minecraft.server.level.ServerPlayer;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.registrymanager.enumquest.EnumQuestRequirement;

import java.io.IOException;

public abstract class AbstractReward {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player        The player
     * @param questTemplate ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException
     * @see EnumQuestRequirement Types of rewards gave on complete a quest
     * @see ModExample Main class where you should register reward's types
     */

    public abstract void handler(ServerPlayer player, ServerQuest questTemplate, int rewardIndex) throws IOException;
}
