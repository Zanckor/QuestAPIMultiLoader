package net.zanckor.questapi.api.file.dialog.codec;

import net.zanckor.questapi.api.file.FileAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ReadConversation extends FileAbstract {

    /**
     * This class allows dev to add dialog to dialog-read.json or get which ones are already read
     * Returns a list of dialogs already read by a certain player
     */
    ConcurrentHashMap<String, List<Integer>> conversations = new ConcurrentHashMap<>();

    public List<Integer> getConversation(String conversationID) {
        conversations.computeIfAbsent(conversationID, dialogList -> new ArrayList<>());

        return conversations.get(conversationID);
    }

    public void setConversations(ConcurrentHashMap<String, List<Integer>> conversations) {
        this.conversations = conversations;
    }

    public void addConversation(String conversationID, Integer dialogID) {
        List<Integer> readDialog = conversations.get(conversationID);
        readDialog.removeIf(id -> id.equals(dialogID));

        readDialog.add(dialogID);

        conversations.put(conversationID, readDialog);
    }
}