package net.zanckor.questapi.mod.common.config.client;

import com.mojang.datafixers.util.Pair;
import net.zanckor.questapi.mod.core.config.ConfigRegistry;
import net.zanckor.questapi.mod.core.config.SimpleConfig;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class ScreenConfig {
    public static SimpleConfig config;
    private static ConfigRegistry configs;

    public static String QUEST_TRACKED_SCREEN;
    public static String QUEST_LOG_SCREEN;

    public static void register() {
        configs = new ConfigRegistry();
        createConfig();

        config = SimpleConfig.of(MOD_ID + "screen_config").provider(configs).request();

        assignConfig();
    }


    private static void createConfig() {
        configs.addPairData(new Pair<>("key.questapi.quest_tracked_screen", MOD_ID), "Which quest tracked design you want to see");
        configs.addPairData(new Pair<>("key.questapi.quest_log_screen", MOD_ID), "Which design you want to see on quest log");
    }

    private static void assignConfig() {
        QUEST_TRACKED_SCREEN = config.get("key.questapi.quest_tracked_screen");
        QUEST_LOG_SCREEN = config.get("key.questapi.quest_log_screen");
    }
}
