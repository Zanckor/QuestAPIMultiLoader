package net.zanckor.questapi.example.common.handler.questrequirement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.server.ServerRequirement;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;

import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestRequirement.XP;

public class XpRequirement extends AbstractQuestRequirement {

    /**
     * Type of requirement that checks if player is between two values of XP
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReq Requirement types
     */

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean handler(Player player, ServerQuest serverQuest, int requirementIndex) throws IOException {
        ServerRequirement serverRequirement = serverQuest.getRequirements().get(requirementIndex);
        String requirementType = serverRequirement.getType() != null ? serverRequirement.getType() : "NONE";
        Enum<?> questRequirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

        if (questRequirementEnum.equals(XP)) {

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

        return false;
    }
}