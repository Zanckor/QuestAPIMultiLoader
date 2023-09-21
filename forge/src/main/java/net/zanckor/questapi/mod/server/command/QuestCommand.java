package net.zanckor.questapi.mod.server.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.file.quest.codec.server.ServerRequirement;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.file.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.zanckor.questapi.mod.common.network.packet.screen.RemovedQuest;
import net.zanckor.questapi.mod.server.startdialog.StartDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.*;

@SuppressWarnings("ConstantConditions")
public class QuestCommand {
    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getPlayer().serverLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = questID + ".json";

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getFailedQuest(userFolder).toString(), quest))) {
            context.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " with UUID " + playerUUID + " already completed/has this quest"));
            return 0;
        }


        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;
            Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks if player has all requirements
            if (serverQuest.getRequirements() != null) {

                for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                    ServerRequirement serverRequirement = serverQuest.getRequirements().get(requirementIndex);
                    String requirementType = serverRequirement.getType() != null ? serverRequirement.getType() : "NONE";
                    Enum<?> questRequirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

                    AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                    if (!requirement.handler(player, serverQuest, requirementIndex)) {
                        return 0;
                    }
                }
            }

            createQuest(serverQuest, player, path);
            QuestDialogManager.registerQuestByID(questID, path);
            SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
            return 1;
        }


        return 0;
    }

    public static void createQuest(ServerQuest serverQuest, Player player, Path path) throws IOException {
        UserQuest userQuest = UserQuest.createQuest(serverQuest, path);

        if (userQuest.hasTimeLimit()) {
            Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
        }

        GsonManager.writeJson(path.toFile(), userQuest);

    }

    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getPlayer().serverLevel();
        Player player = level.getPlayerByUUID(playerUUID);

        Path path = QuestDialogManager.getQuestPathByID(questID);

        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(path.toFile(), UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new RemovedQuest(userQuest.getId()));

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
            Enum<?> goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

            QuestDialogManager.removeQuest(questID, goalEnum);
        }

        path.toFile().delete();

        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
        return 1;
    }

    public static int putDialogToItem(ItemStack item, String dialogId) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("display_dialog", dialogId);

        item.setTag(compoundTag);

        return 1;
    }

    public static int putQuestToItem(ItemStack item, String questId) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("give_quest", questId);

        item.setTag(compoundTag);

        return 1;
    }

    public static int displayDialog(ServerPlayer player, String dialogID) throws IOException {
        StartDialog.loadDialog(player, dialogID, player);

        return 1;
    }
}