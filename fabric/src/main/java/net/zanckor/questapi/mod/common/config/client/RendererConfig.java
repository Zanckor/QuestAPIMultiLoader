package net.zanckor.questapi.mod.common.config.client;

import com.mojang.datafixers.util.Pair;
import net.zanckor.questapi.mod.core.config.ConfigRegistry;
import net.zanckor.questapi.mod.core.config.SimpleConfig;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class RendererConfig {
    public static SimpleConfig config;
    private static ConfigRegistry configs;

    public static String QUEST_MARK_UPDATE_COOLDOWN;

    public static void register(){
        configs = new ConfigRegistry();
        createConfig();

        config = SimpleConfig.of(MOD_ID + "renderer_config").provider(configs).request();

        assignConfig();
    }


    private static void createConfig(){
        configs.addPairData(new Pair<>("key.questapi.quest_mark_update_cooldown", 5), "How long ! mark takes to update on change entity data. \nLower value = Smoothness + Lager");
    }

    private static void assignConfig(){
        QUEST_MARK_UPDATE_COOLDOWN = config.get("key.questapi.quest_mark_update_cooldown");
    }
}
