package net.zanckor.questapi.example.common.handler.dialogrequirement;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCDialog;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DisplayDialog;
import net.zanckor.questapi.mod.common.util.MCUtil;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReqStatus;

import java.io.IOException;

public class DialogRequirement extends AbstractDialogRequirement {

    /**
     * Dialog requirement status called to check if a player has read or not a dialog.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReqStatus Requirement status
     */

    
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";

        // Check if the requirement type is not a dialog requirement
        if (!(requirementType.equals(EnumDialogReq.DIALOG.toString()))) return false;

        // Get the requirement status and dialog ID
        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        int dialog_requirement = requirement.getDialogId();

        // Check the requirement status
        switch (requirementStatus) {
            case READ -> {
                // If the dialog is read by the player and the requirement is READ, display the dialog
                if (MCUtil.isReadDialog(player, dialog_requirement)) {
                    displayDialog(player, option_id, dialog, entity);
                    return true;
                }
            }

            case NOT_READ -> {
                // If the dialog is not read by the player and the requirement is NOT_READ, display the dialog
                if (!MCUtil.isReadDialog(player, option_id)) {
                    displayDialog(player, option_id, dialog, entity);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();

        // Check if the requirement type is not a dialog requirement
        if (!(requirementType.equals(EnumDialogReq.DIALOG.toString()))) return false;

        // Get the requirement status and dialog ID
        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        int dialog_requirement = requirement.getDialogId();

        // Check the requirement status
        switch (requirementStatus) {
            case READ -> {
                // If the dialog is read by the player and the requirement is READ, display the dialog
                if (MCUtil.isReadDialog(player, dialog_requirement)) {
                    displayDialog(player, option_id, dialog, resourceLocation);
                    return true;
                }
            }

            case NOT_READ -> {
                // If the dialog is not read by the player and the requirement is NOT_READ, display the dialog
                if (!MCUtil.isReadDialog(player, option_id)) {
                    displayDialog(player, option_id, dialog, resourceLocation);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();

        // Check if the requirement type is not a dialog requirement
        if (!(requirementType.equals(EnumDialogReq.DIALOG.toString()))) return false;

        // Get the requirement status and dialog ID
        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        int dialog_requirement = requirement.getDialogId();

        // Check the requirement status
        switch (requirementStatus) {
            case READ -> {
                // If the dialog is read by the player and the requirement is READ, display the dialog
                if (MCUtil.isReadDialog(player, dialog_requirement)) {
                    displayDialog(player, option_id, dialog, item);
                    return true;
                }
            }

            case NOT_READ -> {
                // If the dialog is not read by the player and the requirement is NOT_READ, display the dialog
                if (!MCUtil.isReadDialog(player, option_id)) {
                    displayDialog(player, option_id, dialog, item);
                    return true;
                }
            }
        }

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
