package net.zanckor.questapi.example.common.handler.dialogoption;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.dialog.codec.NPCDialog;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DisplayDialog;

import java.io.IOException;

public class DialogOpenDialog extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "OPEN_DIALOG" will try to change current screen to the dialog specified on that option.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     */


    @Override
    public void handler(Player player, Conversation dialog, int option_id, Entity entity) throws IOException {
        int currentDialog = QuestDialogManager.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, entity));
        }
    }

    @Override
    public void handler(Player player, Conversation dialog, int option_id, String resourceLocation) throws IOException {
        int currentDialog = QuestDialogManager.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, resourceLocation));
        }
    }

    @Override
    public void handler(Player player, Conversation dialog, int option_id, Item item) throws IOException {
        int currentDialog = QuestDialogManager.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, item));
        }
    }
}
