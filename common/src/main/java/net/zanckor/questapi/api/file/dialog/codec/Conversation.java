package net.zanckor.questapi.api.file.dialog.codec;

import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.FileAbstract;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public class Conversation extends FileAbstract {
    String conversationID;
    List<NPCDialog.QuestDialog> dialog;
    String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }
    public List<NPCDialog.QuestDialog> getDialog() {
        return dialog;
    }

    public static Conversation createDialog(Path path, String conversationID) {
        Conversation dialogQuest = new Conversation();

        dialogQuest.setConversationID(conversationID);

        QuestDialogManager.registerConversationLocation(conversationID, path);

        return dialogQuest;
    }
}