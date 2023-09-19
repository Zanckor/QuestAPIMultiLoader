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
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReq;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogReqStatus;

import java.io.IOException;

public class NoneRequirement extends AbstractDialogRequirement {

    /**
     * Dialog requirement status called to check if a player has read or not a dialog.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReqStatus Requirement status
     */

// Handler method for player interactions with NPC conversations
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";
        if (!(requirementType.equals(EnumDialogReq.NONE.toString()))) return false;

        // Display the dialog to the player
        displayDialog(player, option_id, dialog, entity);

        // Return true to indicate successful handling of the interaction
        return true;
    }


    // Handler method for player interactions with NPC conversations using a specific resource location
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.NONE.toString()))) return false;

        // Display the dialog to the player using the specified resource location
        displayDialog(player, option_id, dialog, resourceLocation);

        // Return true to indicate successful handling of the interaction
        return true;
    }


    // Handler method for player interactions with NPC conversations using an item
    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        // Get the requirement for the selected dialog option
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.NONE.toString()))) return false;

        // Display the dialog to the player using the specified item
        displayDialog(player, option_id, dialog, item);

        // Return true to indicate successful handling of the interaction
        return true;
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
