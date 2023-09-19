package net.zanckor.questapi.mod.server.startdialog;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCDialog;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.commonutil.GsonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("ConstantConditions")
public class StartDialog {

    /**
     * @param player         The player
     * @param globalDialogID Dialog ID of dialog file player will see. Example: collect_items_dialog.json
     * @throws IOException Exception fired when server cannot read json file
     */

    public static void loadDialog(Player player, String globalDialogID, Entity entity) throws IOException {
        Path path = QuestDialogManager.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        QuestDialogManager.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            NPCDialog.DialogRequirement requirement = dialog.getDialog().get(dialog_id).getServerRequirements();
            String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";

            Enum<?> requirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, entity)) return;
        }
    }
    public static void loadDialog(Player player, String globalDialogID, String resourceLocation) throws IOException {
        Path path = QuestDialogManager.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        QuestDialogManager.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            NPCDialog.DialogRequirement requirement = dialog.getDialog().get(dialog_id).getServerRequirements();
            String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";
            Enum<?> requirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, resourceLocation)) return;
        }
    }

    public static void loadDialog(Player player, String globalDialogID, Item item) throws IOException {
        Path path = QuestDialogManager.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        QuestDialogManager.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            NPCDialog.DialogRequirement requirement = dialog.getDialog().get(dialog_id).getServerRequirements();
            String requirementType = requirement.getType() != null ? requirement.getType() : "NONE";
            Enum<?> requirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, item)) return;
        }
    }
}