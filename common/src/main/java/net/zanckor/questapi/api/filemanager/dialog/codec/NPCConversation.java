package net.zanckor.questapi.api.filemanager.dialog.codec;

import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.FileAbstract;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public class NPCConversation extends FileAbstract {
    private static String global_id;

    List<NPCDialog.QuestDialog> dialog;
    String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGlobal_id() {
        return global_id;
    }

    public List<NPCDialog.QuestDialog> getDialog() {
        return dialog;
    }

    public static NPCConversation createDialog(Path path) {
        NPCConversation dialogQuest = new NPCConversation();

        dialogQuest.setGlobal_id(global_id);

        QuestDialogManager.registerDialogLocation(global_id, path);

        return dialogQuest;
    }

    public void setGlobal_id(String global_id) {
        NPCConversation.global_id = global_id;
    }
}