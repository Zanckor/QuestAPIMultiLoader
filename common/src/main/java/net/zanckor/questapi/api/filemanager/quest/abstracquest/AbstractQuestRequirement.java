package net.zanckor.questapi.api.filemanager.quest.abstracquest;

import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;

import java.io.IOException;

public abstract class AbstractQuestRequirement {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player           The player
     * @param questTemplate    ServerQuestBase with global quest data
     * @param requirementIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestRequirement Types of requirements to access a quest
     * @see ModExample Main class where you should register requirement's types
     */

    public abstract boolean handler(Player player, ServerQuest questTemplate, int requirementIndex) throws IOException;
}
