package net.zanckor.questapi.example.common.handler.questrequirement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerRequirement;
import net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;

import java.io.IOException;

public class XpRequirement extends AbstractQuestRequirement {

    /**
     * Type of requirement that checks if player is between two values of XP
     *
     * @param player           The player
     * @param serverQuest      ServerQuestBase with global quest data
     * @param requirementIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReq Requirement types
     */

    @Override
    public boolean handler(Player player, ServerQuest serverQuest, int requirementIndex) throws IOException {
        ServerRequirement requirement = serverQuest.getRequirements().get(requirementIndex);
        if (player.experienceLevel < 0) player.experienceLevel = 0;

        boolean hasReqs = player.experienceLevel >= requirement.getRequirements_min() && player.experienceLevel <= requirement.getRequirements_max();

        if (!hasReqs) {
            player.sendSystemMessage(Component.literal("Player " + player.getScoreboardName() + " doesn't have the requirements to access to this quest"));
            player.sendSystemMessage(Component.literal("Minimum: " + requirement.getRequirements_min()));
            player.sendSystemMessage(Component.literal("Maximum: " + requirement.getRequirements_max()));
        }

        return hasReqs;
    }
}