package net.zanckor.questapi;

import net.fabricmc.api.ModInitializer;
import net.zanckor.questapi.eventmanager.EventManager;
import net.zanckor.questapi.example.ModExample;
import net.zanckor.questapi.mod.common.config.client.RendererConfig;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;
import net.zanckor.questapi.mod.common.datapack.CompoundTagDialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.DialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.EntityTypeDialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.QuestJSONListener;
import net.zanckor.questapi.mod.common.network.NetworkHandler;

public class FabricQuestAPI implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonMain.init();
        new ModExample();

        registerNetwork();
        registerConfig();
        //registerResourceListener();
        registerCallback();
    }

    private void registerConfig() {
        ScreenConfig.register();
        RendererConfig.register();
    }

    private void registerNetwork() {
        NetworkHandler.registerServerReceiverPacket();
    }

    private void registerResourceListener() {
        new CompoundTagDialogJSONListener().register();
        new EntityTypeDialogJSONListener().register();
        new DialogJSONListener().register();
        new QuestJSONListener().register();
    }

    private void registerCallback() {
        EventManager.registerClass();

        EventManager.serverPlayerEvent();
        EventManager.serverTickEvent();
        EventManager.serverCombatEvent();
        EventManager.serverLifeCycleEvent();
        EventManager.registerCommand();
    }
}
