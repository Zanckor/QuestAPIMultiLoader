package net.zanckor.questapi;

import net.zanckor.questapi.multiloader.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// This class is part of the common project meaning it is shared between all supported loaders.
// Code written here can only import and access the vanilla codebase, libraries used by vanilla
public class CommonMain {
    public static Path serverDirectory, questApi, playerData, serverQuests, serverDialogs, serverNPC, entity_type_list, compoundTag_List;

    // The loader specific projects are able to import and use any code from the common project. This allows dev to
    // write the majority of your code here and load it from your loader specific projects.
    public static void init() {
        Constants.LOG.info("Common init on {}! Currently in a {} environment!", Services.PLATFORM.getPlatform(), Services.PLATFORM.getEnvironmentName());


    }



    public static Path getUserFolder(UUID playerUUID) {
        Path userFolder = Paths.get(playerData.toString(), playerUUID.toString());

        return userFolder;
    }

    public static Path getActiveQuest(Path userFolder) {
        Path activeQuest = Paths.get(userFolder.toString(), "active-quests");

        return activeQuest;
    }

    public static Path getCompletedQuest(Path userFolder) {
        Path completedQuest = Paths.get(userFolder.toString(), "completed-quests");

        return completedQuest;
    }

    public static Path getFailedQuest(Path userFolder) {
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        return uncompletedQuest;
    }

    public static Path getReadDialogs(Path userFolder) {
        Path readDialogs = Paths.get(userFolder.toString(), "read-dialogs");

        return readDialogs;
    }


    public static class Constants {
        public static final String MOD_ID = "questapi";
        public static final String MOD_NAME = "QuestAPI Multiloader";
        public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    }
}