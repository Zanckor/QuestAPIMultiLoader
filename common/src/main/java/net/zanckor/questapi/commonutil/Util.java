package net.zanckor.questapi.commonutil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.common.network.SendQuestPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.*;

public class Util {
    public static boolean isQuestCompleted(UserQuest userQuest) {
        int indexGoals = 0;

        for (UserGoal questGoal : userQuest.getQuestGoals()) {
            indexGoals++;

            if (questGoal.getCurrentAmount() < questGoal.getAmount()) return false;

            if (indexGoals < userQuest.getQuestGoals().size()) continue;

            return true;
        }

        return false;
    }


    public static void moveFileToCompletedFolder(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        String questName = userQuest.getId() + ".json";

        Files.deleteIfExists(Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
        Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            Enum goalEnum = EnumRegistry.getEnum(userQuest.getQuestGoals().get(indexGoals).getType(), EnumRegistry.getQuestGoal());

            QuestDialogManager.movePathQuest(userQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), goalEnum);
        }
    }

    public static void moveFileToUncompletedFolder(Path uncompletedQuestFolder, File file, UserQuest userQuest, Enum goalEnum) throws IOException {
        Path uncompletedPath = Paths.get(uncompletedQuestFolder.toString(), file.getName());

        if (file.exists()) {
            Files.move(file.toPath(), uncompletedPath);
        }
        QuestDialogManager.movePathQuest(userQuest.getId(), uncompletedPath, goalEnum);
    }

    public static Entity getEntityByUUID(ServerLevel level, UUID uuid) {
        for (Entity entity : level.getAllEntities()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }

        return null;
    }

    public static List<Integer> findSlotMatchingItemStack(ItemStack itemStack, Inventory inventory) {
        List<Integer> slots = new ArrayList<>();

        for (int itemSlot = 0; itemSlot < inventory.items.size(); ++itemSlot) {
            if (!inventory.items.get(itemSlot).isEmpty() && ItemStack.isSameItem(itemStack, inventory.items.get(itemSlot))) {
                slots.add(itemSlot);
            }
        }

        return slots;
    }

    public static void addQuest(Player player, String questID) throws IOException {
        String quest = questID + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (hasQuest(quest, userFolder)) {
            for (File file : serverQuests.toFile().listFiles()) {
                if (!(file.getName().equals(quest))) continue;
                Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
                ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

                //Checks if player has all requirements
                for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                    Enum questRequirementEnum = EnumRegistry.getEnum(serverQuest.getRequirements().get(requirementIndex).getType(), EnumRegistry.getQuestRequirement());
                    AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                    if (!requirement.handler(player, serverQuest, requirementIndex)) {
                        return;
                    }
                }

                createQuest(serverQuest, player, path);
                QuestDialogManager.registerQuestByID(questID, path);
                SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
                return;
            }
        }
    }

    public static int createQuest(ServerQuest serverQuest, Player player, Path path) throws IOException {
        UserQuest userQuest = UserQuest.createQuest(serverQuest, path);

        if (userQuest.hasTimeLimit()) {
            Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
        }

        GsonManager.writeJson(path.toFile(), userQuest);

        return 1;
    }

    public static boolean hasQuest(String quest, Path userFolder) {
        return Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getFailedQuest(userFolder).toString(), quest));
    }
}
