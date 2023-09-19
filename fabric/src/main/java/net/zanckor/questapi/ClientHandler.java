package net.zanckor.questapi;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.zanckor.questapi.eventmanager.EventManager;
import net.zanckor.questapi.example.ClientModSetup;
import net.zanckor.questapi.mod.common.network.NetworkHandler;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@SuppressWarnings("all")
public class ClientHandler implements ClientModInitializer {
    public static KeyMapping questMenu;

    @Override
    public void onInitializeClient() {
        registerNetwork();
        ClientHandler.keyInit();
        callBackEvent();

        new ClientModSetup();
    }


    private void callBackEvent() {
        EventManager.registerClass();
        EventManager.clientTickEvent();
        EventManager.clientPlayerEvent();
        EventManager.renderHUDEvent();
        EventManager.renderEntity();
    }

    private void registerNetwork() {
        NetworkHandler.registerClientReceiverPacket();
    }

    //Register keys just with name and keycode
    private static KeyMapping registerKey(String name, int keycode) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key." + MOD_ID + "." + name, keycode, "key.categories.QuestApi"
        ));
    }

    public static void keyInit() {
        questMenu = registerKey("quest_menu", InputConstants.KEY_K);
    }
}
