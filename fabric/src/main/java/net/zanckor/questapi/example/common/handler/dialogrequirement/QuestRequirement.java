package net.zanckor.questapi.example.common.handler.dialogrequirement;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCDialog;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DisplayDialog;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReqStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


@SuppressWarnings("ConstantConditions")
public class QuestRequirement extends AbstractDialogRequirement {

    /**
     * Quest requirement status called to check if a player has a quest, is completed, or never obtained it before.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     *                     IMPORTANT If quest is removed it will be automatically set as "NOT_OBTAINED"
     * @see EnumDialogReqStatus Requirement status
     */


// Handler method for player interactions with NPC conversations rendering an Entity
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        if (player.level().isClientSide) return false;

        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";

        if (!(requirementType.equals(EnumDialogReq.QUEST.toString()))) return false;

        // Get the requirement status and quest path
        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        Path questPath = QuestDialogManager.getQuestByID(requirement.getQuestId());

        //If questPath is null means player hasn't read any dialog, so if requirement status is NOT_OBTAINED always will be true
        if (questPath == null) {
            // If the requirement status is NOT_OBTAINED, display the dialog
            if (requirementStatus == EnumDialogReqStatus.NOT_OBTAINED) {
                displayDialog(player, option_id, dialog, entity);

                return true;
            }
        } else {
            File questFile = questPath.toFile();
            UserQuest playerQuest = questFile.exists() ? (UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class) : null;

            // Check the requirement status
            switch (requirementStatus) {
                case IN_PROGRESS -> {
                    // If the quest file exists and is not completed, display the dialog
                    if (questFile.exists() && !playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, entity);

                        return true;
                    }
                }

                case COMPLETED -> {
                    // If the quest file exists and is completed, display the dialog
                    if (questFile.exists() && playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, entity);

                        return true;
                    }
                }

                case NOT_OBTAINED -> {
                    // If the quest file does not exist, display the dialog
                    if (!questFile.exists()) {
                        displayDialog(player, option_id, dialog, entity);

                        return true;
                    }
                }
            }
        }

        // If none of the conditions are met, return false
        return false;
    }


    // Handler method for player interactions with NPC conversations using a specific resource location for rendering an entity
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        if (player.level().isClientSide) return false;

        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.QUEST.toString()))) return false;

        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        Path questPath = QuestDialogManager.getQuestByID(requirement.getQuestId());

        //If questPath is null means player hasn't read any dialog, so if requirement status is NOT_OBTAINED always will be true
        if (questPath == null) {
            // If the requirement status is NOT_OBTAINED, display the dialog
            if (requirementStatus == EnumDialogReqStatus.NOT_OBTAINED) {
                displayDialog(player, option_id, dialog, resourceLocation);

                return true;
            }
        } else {
            File questFile = questPath.toFile();
            UserQuest playerQuest = questFile.exists() ? (UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class) : null;

            // Check the requirement status
            switch (requirementStatus) {
                case IN_PROGRESS -> {
                    // If the quest file exists and is not completed, display the dialog
                    if (questFile.exists() && !playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, resourceLocation);

                        return true;
                    }
                }

                case COMPLETED -> {
                    // If the quest file exists and is completed, display the dialog
                    if (questFile.exists() && playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, resourceLocation);

                        return true;
                    }
                }

                case NOT_OBTAINED -> {
                    // If the quest file does not exist, display the dialog
                    if (!questFile.exists()) {
                        displayDialog(player, option_id, dialog, resourceLocation);

                        return true;
                    }
                }
            }
        }

        // If none of the conditions are met, return false
        return false;
    }



    // Handler method for player interactions with NPC conversations using a specific resource location for rendering an item
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        if (player.level().isClientSide) return false;

        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.QUEST.toString()))) return false;

        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        Path questPath = QuestDialogManager.getQuestByID(requirement.getQuestId());

        //If questPath is null means player hasn't read any dialog, so if requirement status is NOT_OBTAINED always will be true
        if (questPath == null) {
            // If the requirement status is NOT_OBTAINED, display the dialog
            if (requirementStatus == EnumDialogReqStatus.NOT_OBTAINED) {
                displayDialog(player, option_id, dialog, item);

                return true;
            }
        } else {
            File questFile = questPath.toFile();
            UserQuest playerQuest = questFile.exists() ? (UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class) : null;

            // Check the requirement status
            switch (requirementStatus) {
                case IN_PROGRESS -> {
                    // If the quest file exists and is not completed, display the dialog
                    if (questFile.exists() && !playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, item);

                        return true;
                    }
                }

                case COMPLETED -> {
                    // If the quest file exists and is not completed, display the dialog
                    if (questFile.exists() && playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, item);

                        return true;
                    }
                }

                case NOT_OBTAINED -> {
                    // If the quest file does not exist, display the dialog
                    if (!questFile.exists()) {
                        displayDialog(player, option_id, dialog, item);

                        return true;
                    }
                }
            }
        }

        // If none of the conditions are met, return false
        return false;
    }


    private void displayDialog(Player player, int dialog_id, NPCConversation dialog, Entity entity) throws IOException {
        QuestDialogManager.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), dialog_id, player, entity));
    }

    private void displayDialog(Player player, int dialog_id, NPCConversation dialog, String resourceLocation) throws IOException {
        QuestDialogManager.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), dialog_id, player, resourceLocation));
    }

    private void displayDialog(Player player, int dialog_id, NPCConversation dialog, Item item) throws IOException {
        QuestDialogManager.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), dialog_id, player, item));
    }
}
