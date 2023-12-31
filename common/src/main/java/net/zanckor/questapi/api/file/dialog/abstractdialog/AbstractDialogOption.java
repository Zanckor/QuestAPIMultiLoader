package net.zanckor.questapi.api.file.dialog.abstractdialog;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;

import java.io.IOException;

public abstract class AbstractDialogOption {

    /**
     * Abstract class to call a registered dialog option
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     */

    public abstract void handler(Player player, Conversation dialog, int option_id, Entity entity) throws IOException;
    public abstract void handler(Player player, Conversation dialog, int option_id, String resourceLocation) throws IOException;
    public abstract void handler(Player player, Conversation dialog, int option_id, Item item) throws IOException;
}
