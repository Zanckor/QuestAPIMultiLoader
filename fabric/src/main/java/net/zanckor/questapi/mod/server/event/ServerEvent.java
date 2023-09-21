package net.zanckor.questapi.mod.server.event;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.npc.entity_type_tag.codec.EntityTypeTagDialog;
import net.zanckor.questapi.api.file.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.file.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.registry.EnumRegistry;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerConnectionServerEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.npcmarker.ValidNPCMarker;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.zanckor.questapi.mod.common.util.MCUtil;
import net.zanckor.questapi.mod.core.data.IEntityData;
import net.zanckor.questapi.mod.server.startdialog.StartDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.*;

@EventSubscriber
public class ServerEvent {

    @SubscribeEvent(event = PlayerEvent.PlayerTickEvent.class)
    @SuppressWarnings("ConstantConditions")
    public static void questWithTimer(Player player) throws IOException {
        if (player.getServer() == null || player.getServer().getTickCount() % 20 != 0) {
            return;
        }

        Path activeQuest = getActiveQuest(getUserFolder(player.getUUID()));
        Path uncompletedQuest = getFailedQuest(getUserFolder(player.getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

            if (userQuest != null) {
                timer(userQuest, player, file, uncompletedQuest);
            }
        }
    }

    public static void timer(UserQuest userQuest, Player player, File file, Path uncompletedQuest) throws IOException {
        if (userQuest == null) {
            LOG.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        //If quest timer is finished, change to uncompleted player's quest
        if (!userQuest.isCompleted() && userQuest.hasTimeLimit() && Timer.canUseWithCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds())) {
            userQuest.setCompleted(true);
            GsonManager.writeJson(file, userQuest);

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
                Enum<?> goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

                MCUtil.moveFileToUncompletedFolder(uncompletedQuest, file, userQuest, goalEnum);
            }

            SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
        }
    }

    @SubscribeEvent(event = PlayerConnectionServerEvent.PlayerLeaveServer.class)
    @SuppressWarnings("ConstantConditions")
    public static void uncompletedQuestOnLogOut(Player player) throws IOException {
        Path activeQuest = getActiveQuest(getUserFolder(player.getUUID()));
        Path uncompletedQuest = getFailedQuest(getUserFolder(player.getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
            if (userQuest == null || !userQuest.hasTimeLimit()) continue;

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
                Enum<?> goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

                userQuest.setCompleted(true);
                GsonManager.writeJson(file, userQuest);

                MCUtil.moveFileToUncompletedFolder(uncompletedQuest, file, userQuest, goalEnum);
            }
        }
    }

    @SubscribeEvent(event = PlayerConnectionServerEvent.PlayerJoinServer.class)
    @SuppressWarnings("ConstantConditions")
    public static void loadHashMaps(MinecraftServer server, Player player) throws IOException {
        LOG.info("Loading hash maps with quests and dialogs for player " + player.getName());

        Path userFolder = getUserFolder(player.getUUID());
        Path activeQuest = getActiveQuest(userFolder);
        Path completedQuest = getCompletedQuest(userFolder);
        Path uncompletedQuest = getFailedQuest(userFolder);

        Path[] questPaths = {activeQuest, completedQuest, uncompletedQuest};

        for (Path path : questPaths) {
            if (path.toFile().listFiles() != null) {

                for (File file : path.toFile().listFiles()) {
                    UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                    if (userQuest == null) continue;

                    for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
                        Enum<?> goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

                        QuestDialogManager.registerQuestTypeLocation(goalEnum, file.toPath().toAbsolutePath());
                    }

                    QuestDialogManager.registerQuestByID(file.getName().substring(0, file.getName().length() - 5), file.toPath().toAbsolutePath());
                }
            }
        }


        if (serverDialogs.toFile().listFiles() != null) {
            for (File file : serverDialogs.toFile().listFiles()) {
                QuestDialogManager.registerConversationLocation(file.getName(), file.toPath().toAbsolutePath());
            }
        }


        SendQuestPacket.TO_CLIENT(player, new ValidNPCMarker());
        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
    }

    @SubscribeEvent(event = PlayerInteractEvent.PlayerInteractItem.class)
    public static void loadDialogOrAddQuestViaItem(Player player, ItemStack itemStack) throws IOException {
        final CompoundTag TAG = itemStack.getTag();
        if (TAG != null && TAG.contains("display_dialog")) {
            String dialogID = TAG.getString("display_dialog");

            StartDialog.loadDialog(player, dialogID, itemStack.getItem());
        }

        if (TAG != null && TAG.contains("give_quest")) {
            String questID = TAG.getString("give_quest");

            MCUtil.addQuest(player, questID);
        }
    }


    @SubscribeEvent(event = PlayerInteractEvent.PlayerInteractEntity.class)
    public static void loadDialogPerEntityType(Player player, Entity entity, InteractionHand interactionHand) throws IOException {
        String targetEntityType = EntityType.getKey(entity.getType()).toString();

        List<String> dialogPerEntityType = QuestDialogManager.getDialogPathByEntityType(targetEntityType);

        if (!player.level().isClientSide && dialogPerEntityType != null && interactionHand.equals(InteractionHand.MAIN_HAND) && openVanillaMenu(player)) {
            String selectedDialog = ((IEntityData) entity).getPersistentData().getString("dialog");

            if (((IEntityData) entity).getPersistentData().get("dialog") == null) {
                int selectedInteger = MCUtil.randomBetween(0, dialogPerEntityType.size());
                selectedDialog = dialogPerEntityType.get(selectedInteger);

                ((IEntityData) entity).getPersistentData().putString("dialog", selectedDialog);
            }

            StartDialog.loadDialog(player, selectedDialog, entity);
        }
    }


    @SubscribeEvent(event = PlayerInteractEvent.PlayerInteractEntity.class)
    @SuppressWarnings("ConstantConditions")
    public static void loadDialogPerCompoundTag(Player player, Entity entity, InteractionHand interactionHand) throws IOException {
        List<String> dialogs = new ArrayList<>();

        if (interactionHand.equals(InteractionHand.MAIN_HAND) && openVanillaMenu(player)) {

            for (Map.Entry<String, File> entry : QuestDialogManager.conversationByrCompoundTag.entrySet()) {
                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(entity);
                File value = entry.getValue();
                EntityTypeTagDialog entityTypeDialog = (EntityTypeTagDialog) GsonManager.getJsonClass(value, EntityTypeTagDialog.class);

                conditions:
                for (EntityTypeTagDialog.EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;

                    switch (conditions.getLogic_gate()) {
                        case OR -> {
                            for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                if (entityNBT.get(nbt.getTag()) == null) {
                                    tagCompare = false;
                                    continue;
                                }

                                tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());

                                if (tagCompare) {
                                    dialogs.addAll(conditions.getDialog_list());

                                    continue conditions;
                                }
                            }
                        }
                        case AND -> {
                            boolean shouldAddDialogList = false;

                            for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {

                                if (entityNBT.get(nbt.getTag()) != null) {
                                    tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());
                                } else {
                                    tagCompare = false;
                                }

                                shouldAddDialogList = tagCompare;

                                if (!tagCompare) break;
                            }

                            if (shouldAddDialogList) {
                                dialogs.addAll(conditions.getDialog_list());
                            }

                        }
                    }
                }
            }

            if (!dialogs.isEmpty()) {
                String selectedDialog = ((IEntityData) entity).getPersistentData().getString("dialog");

                if (((IEntityData) entity).getPersistentData().get("dialog") == null && !dialogs.isEmpty()) {
                    int selectedInteger = MCUtil.randomBetween(0, dialogs.size());
                    selectedDialog = dialogs.get(selectedInteger);

                    ((IEntityData) entity).getPersistentData().putString("dialog", selectedDialog);
                }

                StartDialog.loadDialog(player, selectedDialog, entity);
            }
        }
    }


    public static boolean openVanillaMenu(Player player) {
        if (player.isShiftKeyDown()) {
            player.setShiftKeyDown(false);
            return false;
        }

        return true;
    }
}
