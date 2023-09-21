package net.zanckor.questapi.example.common.handler.questrequirement;

import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;

import java.io.IOException;

public class NoneRequirement extends AbstractQuestRequirement {

    /**
     * If requirement is empty or NONE, it will always give the quest
     *
     * @param player           The player
     * @param serverQuest      ServerQuestBase with global quest data
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReq Requirement types
     */

    @Override
    public boolean handler(Player player, ServerQuest serverQuest, int requirementIndex) throws IOException {
        return true;
    }
}