package net.zanckor.questapi.api.screenmanager;

import java.util.HashMap;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class ScreenRegistry {
    /**
     * This class has registries whichever screen you want to display to screens
     */

    private static HashMap<String, AbstractDialog> dialog = new HashMap<>();
    private static HashMap<String, AbstractQuestTracked> tracked_screen = new HashMap<>();
    private static HashMap<String, AbstractQuestLog> quest_log_screen = new HashMap<>();


    /**
     * Registry dialog - Display dialog screen
     */
    public static void registerDialogScreen(String identifier, AbstractDialog screen) {
        dialog.put(identifier, screen);
    }

    public static AbstractDialog getDialogScreen(String identifier) {
        AbstractDialog abstractDialog = dialog.get(identifier);

        if (abstractDialog != null) {
            return abstractDialog;
        }

        errorMessage(identifier);
        return dialog.get(MOD_ID);
    }

    /**
     * Registry quest tracked screen - Display tracked quest data on HUD
     */

    public static void registerQuestTrackedScreen(String identifier, AbstractQuestTracked screen) {
        tracked_screen.put(identifier, screen);
    }

    public static AbstractQuestTracked getQuestTrackedScreen(String identifier) {
        AbstractQuestTracked trackedScreen = tracked_screen.get(identifier);

        if (trackedScreen != null) {
            return trackedScreen;
        }

        errorMessage(identifier);
        return tracked_screen.get(MOD_ID);
    }


    /**
     * Registry quest log screen - Shows active quest and quest data
     */
    public static void registerQuestLogScreen(String identifier, AbstractQuestLog screen) {
        quest_log_screen.put(identifier, screen);
    }

    public static AbstractQuestLog getQuestLogScreen(String identifier) {
        AbstractQuestLog questLogScreen = quest_log_screen.get(identifier);

        if (questLogScreen != null) {
            return questLogScreen;
        }

        errorMessage(identifier);
        return quest_log_screen.get(QuestApiMain.MOD_ID);
    }


    private static void errorMessage(String identifier) {
        QuestApiMain.LOGGER.error("Your identifier " + identifier + " is incorrect or you have no screen registered");
    }
}
