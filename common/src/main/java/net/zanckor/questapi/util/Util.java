package net.zanckor.questapi.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.api.file.dialog.codec.ReadConversation;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.registry.EnumRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.zanckor.questapi.CommonMain.*;

@SuppressWarnings("unused")
public class Util {

    public static ConcurrentHashMap<String, Integer> lastDialogPerConversation = new ConcurrentHashMap<>();


    public static boolean isQuestCompleted(UserQuest userQuest) {
        int indexGoals = 0;

        // Iterate through the quest goals for the userQuest
        for (UserGoal questGoal : userQuest.getQuestGoals()) {
            indexGoals++; //Increment the index for each goal

            // Check if the current amount of the goal is less than the required amount
            if (questGoal.getCurrentAmount() < questGoal.getAmount()) return false;

            // Check if there are more goals to iterate through
            if (indexGoals < userQuest.getQuestGoals().size())
                continue;  // If so, continue to the next goal

            // If this point is reached, it means all goals have been completed
            return true;
        }

        // If no goals were found or all goals were incomplete, return false
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isConversationCompleted(String conversationID, Player player) throws IOException {
        // Get the user folder for the player
        Path userFolder = Paths.get(CommonMain.playerData.toString(), player.getUUID().toString());

        // Get the path for the read dialog file
        Path readDialogPath = Paths.get(CommonMain.getReadDialogs(userFolder).toString(), File.separator, "dialog_read.json");
        File readDialogFile = readDialogPath.toFile();

        // Deserialize the readConversation and conversation from JSON files
        Optional<ReadConversation> readConversation = readDialogFile.exists() ? Optional.ofNullable((ReadConversation) GsonManager.getJsonClass(readDialogFile, ReadConversation.class)) : Optional.empty();
        List<Integer> dialogIdList = readConversation.orElse(new ReadConversation()).getConversation(conversationID);

        Conversation conversation = QuestDialogManager.getConversationByID(conversationID);

        // Check if the highest DialogID in readDialog equals with the last dialogID of conversation
        int highestDialogID = 0;

        for (Integer dialogID : dialogIdList) {
            highestDialogID = Math.max(highestDialogID, dialogID);
        }
        lastDialogPerConversation.put(conversationID, highestDialogID);

        //If lastDialogID equals to the last dialogID of the conversation, it has been completed
        return (highestDialogID >= conversation.getDialog().size());
    }

    public static void moveFileToCompletedFolder(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        // Get the user folder based on the player's UUID
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());

        // Construct the path to the completed quest folder for the file
        Path completedPath = Paths.get(getCompletedQuest(userFolder).toString(), file.getName());

        // Extract the quest ID from the file name (assuming file name ends with ".json")
        String id = file.getName().substring(0, file.getName().length() - 5);

        // Delete the file if it already exists in the completed quest folder and move the file to completed quest folder
        Files.deleteIfExists(Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
        Files.move(file.toPath(), completedPath);

        // Iterate through the quest goals
        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            // Get the goal enum based on the goal type
            Enum<?> goalEnum = EnumRegistry.getEnum(userQuest.getQuestGoals().get(indexGoals).getType(), EnumRegistry.getQuestGoal());

            // Move the path related to the goal within the completed quest folder
            QuestDialogManager.movePathQuest(id, Paths.get(getCompletedQuest(userFolder).toString(), file.getName()), goalEnum);
        }
    }

    public static void moveFileToUncompletedFolder(Path uncompletedQuestFolder, File file, UserQuest userQuest, Enum<?> goalEnum) throws IOException {
        // Construct the path to the uncompleted quest folder for the file
        Path uncompletedPath = Paths.get(uncompletedQuestFolder.toString(), file.getName());

        // Extract the quest ID from the file name (assuming file name ends with ".json")
        String id = file.getName().substring(0, file.getName().length() - 5);

        // Check if the file exists and move it to uncompleted quest folder
        if (file.exists()) {
            Files.move(file.toPath(), uncompletedPath);
        }

        // Trigger actions related to moving the path associated with the quest and goal within the uncompleted quest folder
        QuestDialogManager.movePathQuest(id, uncompletedPath, goalEnum);
    }

    public static Entity getEntityByUUID(ServerLevel level, UUID uuid) {
        // Iterate through all entities in the server level
        for (Entity entity : level.getAllEntities()) {

            // Check if the entity's UUID matches the provided UUID and return the entity
            if (entity.getUUID().equals(uuid)) return entity;
        }

        // If no entity with the provided UUID is found, return null
        return null;
    }

    public static List<Integer> findSlotMatchingItemStack(ItemStack itemStack, Inventory inventory) {
        List<Integer> slots = new ArrayList<>();

        // Iterate through inventory slots
        for (int itemSlot = 0; itemSlot < inventory.items.size(); ++itemSlot) {
            // Check if the slot is not empty and contains an ItemStack matching the provided one
            if (!inventory.items.get(itemSlot).isEmpty() && ItemStack.isSameItem(itemStack, inventory.items.get(itemSlot))) {

                slots.add(itemSlot); // Add the slot to the list
            }
        }

        return slots;
    }

    public static void createQuest(ServerQuest serverQuest, Player player, Path path) throws IOException {
        // Create a UserQuest based on the ServerQuest and path
        UserQuest userQuest = UserQuest.createQuest(serverQuest, path);

        // If the user quest has a time limit, update the player's cooldown
        if (userQuest.hasTimeLimit()) {
            Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
        }

        GsonManager.writeJson(path.toFile(), userQuest); // Write the user quest to a JSON file
    }

    public static int getFreeSlots(Player player) {
        Inventory inventory = player.getInventory();
        int freeSlotQuantity = 0;

        // Iterate through inventory slots
        for (int slot = 0; slot < inventory.items.size(); ++slot) {
            // Check if the slot is empty
            if (inventory.items.get(slot).isEmpty()) {
                freeSlotQuantity++; // Increment the count of free slots
            }
        }

        return freeSlotQuantity; // Return the number of free slots
    }


    public static boolean hasQuest(String quest, Path userFolder) {
        // Check if the quest exists in any of the quest folders
        return Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) ||
                Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) ||
                Files.exists(Paths.get(getFailedQuest(userFolder).toString(), quest));
    }
}
