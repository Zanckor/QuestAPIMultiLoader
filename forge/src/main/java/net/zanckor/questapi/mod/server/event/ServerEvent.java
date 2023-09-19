package net.zanckor.questapi.mod.server.event;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog;
import net.zanckor.questapi.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.npcmarker.ValidNPCMarker;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.zanckor.questapi.mod.common.util.MCUtil;
import net.zanckor.questapi.mod.server.startdialog.StartDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.*;
import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvent {

    @SubscribeEvent
    public static void questWithTimer(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level().isClientSide) {
            return;
        }

        Path activeQuest = getActiveQuest(getUserFolder(e.player.getUUID()));
        Path uncompletedQuest = getFailedQuest(getUserFolder(e.player.getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

            if (userQuest != null) {
                timer(userQuest, e.player, file, uncompletedQuest);
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

    @SubscribeEvent
    public static void uncompletedQuestOnLogOut(PlayerEvent.PlayerLoggedOutEvent e) throws IOException {
        Path activeQuest = getActiveQuest(getUserFolder(e.getEntity().getUUID()));
        Path uncompletedQuest = getFailedQuest(getUserFolder(e.getEntity().getUUID()));

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


    @SubscribeEvent
    public static void loadHashMaps(PlayerEvent.PlayerLoggedInEvent e) throws IOException {
        LOG.info("Loading hash maps with quests and dialogs for player " + e.getEntity().getName());


        Path userFolder = getUserFolder(e.getEntity().getUUID());
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
                QuestDialogManager.registerDialogLocation(file.getName(), file.toPath().toAbsolutePath());
            }
        }


        SendQuestPacket.TO_CLIENT(e.getEntity(), new ValidNPCMarker());
        SendQuestPacket.TO_CLIENT(e.getEntity(), new ActiveQuestList(e.getEntity().getUUID()));
    }

    @SubscribeEvent
    public static void loadDialogOrAddQuestViaItem(PlayerInteractEvent e) throws IOException {
        final ItemStack ITEM_STACK = e.getItemStack();
        final Player PLAYER = e.getEntity();
        final CompoundTag TAG = ITEM_STACK.getTag();

        if (e.getSide().isClient() || ITEM_STACK == null || TAG == null) return;

        if (TAG.contains("display_dialog")) {
            String dialogID = TAG.getString("display_dialog");

            StartDialog.loadDialog(PLAYER, dialogID, e.getItemStack().getItem());
        }

        if (TAG.contains("give_quest")) {
            String questID = TAG.getString("give_quest");

            MCUtil.addQuest(PLAYER, questID);
        }
    }

    @SubscribeEvent
    public static void loadDialogPerEntityType(PlayerInteractEvent.EntityInteract e) throws IOException {
        Player player = e.getEntity();
        Entity target = e.getTarget();
        String targetEntityType = EntityType.getKey(target.getType()).toString();

        List<String> dialogPerEntityType = QuestDialogManager.getDialogPerEntityType(targetEntityType);

        if (!player.level().isClientSide && dialogPerEntityType != null && e.getHand().equals(InteractionHand.MAIN_HAND) && openVanillaMenu(player)) {
            String selectedDialog = target.getPersistentData().getString("dialog");

            if (target.getPersistentData().get("dialog") == null) {
                int selectedInteger = MCUtil.randomBetween(0, dialogPerEntityType.size());
                selectedDialog = dialogPerEntityType.get(selectedInteger);

                target.getPersistentData().putString("dialog", selectedDialog);
            }

            StartDialog.loadDialog(player, selectedDialog, target);
        }
    }

    @SubscribeEvent
    public static void loadDialogPerCompoundTag(PlayerInteractEvent.EntityInteract e) throws IOException {
        Player player = e.getEntity();
        Entity target = e.getTarget();
        List<String> dialogs = new ArrayList<>();

        if (!player.level().isClientSide && e.getHand().equals(InteractionHand.MAIN_HAND) && openVanillaMenu(player)) {

            for (Map.Entry<String, File> entry : QuestDialogManager.dialogPerCompoundTag.entrySet()) {
                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(target);
                File value = entry.getValue();
                EntityTypeTagDialog entityTypeDialog = (EntityTypeTagDialog) GsonManager.getJsonClass(value, EntityTypeTagDialog.class);

                conditions:
                for (EntityTypeTagDialog.EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;

                    switch (conditions.getLogic_gate()) {
                        case OR -> {
                            for (EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
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

                            for (EntityTypeTagDialogNBT nbt : conditions.getNbt()) {

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
                e.setCanceled(true);

                String selectedDialog = target.getPersistentData().getString("dialog");

                if (target.getPersistentData().get("dialog") == null && !dialogs.isEmpty()) {
                    int selectedInteger = MCUtil.randomBetween(0, dialogs.size());
                    selectedDialog = dialogs.get(selectedInteger);

                    target.getPersistentData().putString("dialog", selectedDialog);
                }

                StartDialog.loadDialog(player, selectedDialog, target);
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