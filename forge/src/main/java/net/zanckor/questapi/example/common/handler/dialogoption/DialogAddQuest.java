package net.zanckor.questapi.example.common.handler.dialogoption;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCConversation;
import net.zanckor.questapi.api.filemanager.dialog.codec.NPCDialog;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.zanckor.questapi.mod.common.network.message.dialogoption.CloseDialog;
import net.zanckor.questapi.mod.common.network.message.quest.ActiveQuestList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.zanckor.questapi.CommonMain.*;

public class DialogAddQuest extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "ADD_QUEST" will try to give it if player has the requirements.
     * In case that player obtains the quest, a new file will be written on his folder data.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     */

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        int currentDialog = QuestDialogManager.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);
        String quest = option.getQuest_id() + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (!(option.getType().equals(EnumDialogOption.ADD_QUEST.toString()))) return;
        if (Util.hasQuest(quest, userFolder)) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::closeDialog);
            return;
        }


        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;

            Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks all quest requirements and return if player hasn't any requirement
            for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                Enum questRequirementEnum = EnumRegistry.getEnum(serverQuest.getRequirements().get(requirementIndex).getType(), EnumRegistry.getQuestRequirement());
                AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                if (!requirement.handler(player, serverQuest, requirementIndex)) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::closeDialog);
                    return;
                }
            }

            UserQuest userQuest = UserQuest.createQuest(serverQuest, path);
            GsonManager.writeJson(path.toFile(), userQuest);

            if (userQuest.hasTimeLimit()) {
                Timer.updateCooldown(player.getUUID(), option.getQuest_id(), userQuest.getTimeLimitInSeconds());
            }

            QuestDialogManager.registerQuestByID(option.getQuest_id(), path);

            break;
        }

        //Close screen and update active quest list on client side
        SendQuestPacket.TO_CLIENT(player, new CloseDialog());
        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        handler(player, dialog, option_id, (Entity) null);
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        handler(player, dialog, option_id, (Entity) null);
    }
}
