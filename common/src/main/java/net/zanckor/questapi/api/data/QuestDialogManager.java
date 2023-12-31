package net.zanckor.questapi.api.data;

import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.npc.entity_type.codec.EntityTypeDialog;
import net.zanckor.questapi.util.GsonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class QuestDialogManager {

    /**
     * This class stores server quests and dialogs as temporary data to improve access time.
     *
     * <p>
     * <code> currentDialog: </code> Stores dialog id of a text displayed to a player. It's equal to the id in the dialog.json file.
     * </p>
     *
     * <p>
     * <code> currentGlobalDialog: </code> Stores the path to a json file that have dialog information. Example: collect_items_dialog.json
     * </p>
     *
     * <p>
     * <code> clientQuestByIDLocation: </code> A path to access quests faster by quest id. Only active quests are stored, not completed or uncompleted quests.
     * </p>
     *
     * <p>
     * <code> clientQuestTypeLocation: </code> A list of paths referenced to an Enum that stores all quests of Enum type.
     * </p>
     *
     * <p>
     * <code> dialogLocation </code>: A path referenced to dialog ID.
     * </p>
     *
     * <p>
     * <code> dialogPerEntityType </code>: List of dialogs that an Entity Type will display based on npc/entity_type files.
     * </p>
     *
     * <p> <p>
     * As a developer you shouldn't add stuff here to registry more types of quests, dialogs, rewards, etc.
     * </p>
     * <p>
     * ClientQuestTypeLocation returns a list Path of all quests of Enum type
     */


    public static HashMap<Player, Integer> currentDialog = new HashMap<>();
    public static HashMap<Player, String> currentGlobalDialog = new HashMap<>();
    public static HashMap<String, Path> clientQuestByIDLocation = new HashMap<>();
    public static HashMap<Enum<?>, List<Path>> clientQuestTypeLocation = new HashMap<>();
    public static HashMap<String, Path> conversationLocation = new HashMap<>();
    public static HashMap<String, List<String>> conversationByEntityType = new HashMap<>();
    public static HashMap<String, File> conversationByCompoundTag = new HashMap<>();

    public static HashMap<String, Conversation> conversationByID = new HashMap<>();


    public static void registerDialogByCompoundTag() {
        for (File file : CommonMain.compoundTag_List.toFile().listFiles()) {
            conversationByCompoundTag.put(file.getName(), file);
        }
    }

    public static void registerDialogByEntityType() throws IOException {
        List<String> dialogs = new ArrayList<>();

        //Checks each file inside entity_type_list folder and turns them into EntityTypeDialog.class
        for (File file : CommonMain.entity_type_list.toFile().listFiles()) {
            EntityTypeDialog entityTypeDialog = (EntityTypeDialog) GsonManager.getJsonClass(file, EntityTypeDialog.class);

            //Saves each EntityType and their available dialogs into an HashMap
            for (String entity_type : entityTypeDialog.getEntity_type()) {
                dialogs.addAll(entityTypeDialog.getDialog_list());

                conversationByEntityType.put(entity_type, dialogs);
            }
        }
    }

    public static void registerQuestTypeLocation(Enum<?> type, Path path) {
        clientQuestTypeLocation.computeIfAbsent(type, anEnum -> new ArrayList<>());
        if (getQuestTypePathLocation(type).contains(path)) return;

        List<Path> questList = clientQuestTypeLocation.get(type);
        questList.add(path);

        clientQuestTypeLocation.put(type, questList);
    }

    public static void registerQuestByID(String id, Path path) {
        clientQuestByIDLocation.put(id, path);
    }

    public static void registerConversationLocation(String id, Path path) {
        String conversationID = id.substring(0, id.length() - 5);

        conversationLocation.put(conversationID, path);

        try {
            Conversation conversation = (Conversation) GsonManager.getJsonClass(path.toFile(), Conversation.class);
            conversationByID.put(conversationID, conversation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unused")
    public static File getDialogPathByCompoundTag(String compoundTag) {
        if (!conversationByCompoundTag.containsKey(compoundTag)) return null;

        return conversationByCompoundTag.get(compoundTag);
    }

    public static List<String> getConversationByEntityType(String entityType) {
        if (!conversationByEntityType.containsKey(entityType)) return null;

        return conversationByEntityType.get(entityType);
    }

    public static List<Path> getQuestTypePathLocation(Enum<?> type) {
        return clientQuestTypeLocation.get(type);
    }

    public static Path getQuestPathByID(String id) {
        return clientQuestByIDLocation.get(id);
    }

    public static Path getConversationPathLocation(String conversationID) {
        return conversationLocation.get(conversationID);
    }

    public static Conversation getConversationByID(String id) {
        return conversationByID.get(id);
    }

    public static void movePathQuest(String id, Path newPath, Enum<?> enumQuestType) {
        removeQuest(id, enumQuestType);

        registerQuestTypeLocation(enumQuestType, newPath);
        registerQuestByID(id, newPath);
    }

    public static void removeQuest(String id, Enum<?> enumQuestType) {
        List<Path> oldPathList = getQuestTypePathLocation(enumQuestType);

        oldPathList.removeIf(listId -> (listId.toString().contains(id + ".json")));

        clientQuestTypeLocation.replace(enumQuestType, oldPathList, oldPathList);
        clientQuestByIDLocation.remove(id);
    }

}