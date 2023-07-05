package net.zanckor.questapi.example.common.handler.dialogoption;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCDialog;
import net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.message.dialogoption.CloseDialog;

import java.io.IOException;

public class DialogCloseDialog extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "CLOSE_DIALOG" will close the screen.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     */

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        int currentDialog = QuestDialogManager.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.CLOSE_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new CloseDialog());
        }
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        handler(player, dialog, option_id, (Entity) null);
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        handler(player, dialog, option_id, (Entity) null);
    }
}
