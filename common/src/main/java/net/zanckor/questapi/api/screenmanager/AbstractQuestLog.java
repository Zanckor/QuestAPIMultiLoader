package net.zanckor.questapi.api.screenmanager;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public abstract class AbstractQuestLog extends Screen {

    protected AbstractQuestLog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen() throws IOException;
}
