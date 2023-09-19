package net.zanckor.questapi.mod.common.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.screenmanager.AbstractDialog;
import net.zanckor.questapi.api.screenmanager.NpcType;
import net.zanckor.questapi.api.screenmanager.ScreenRegistry;
import net.zanckor.questapi.commonutil.Timer;
import net.zanckor.questapi.mod.common.util.MCUtilClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ConstantConditions, rawtypes")
public class NetworkClientHandler {
    public static List<UserQuest> trackedQuestList = new ArrayList<>();
    public static List<UserQuest> activeQuestList;

    public static List<EntityType> availableEntityTypeForQuest = new ArrayList<>();
    public static Map<String, String> availableEntityTagForQuest = new HashMap<>();

    public static void toastQuestCompleted(String questName) {
        String title = I18n.get(questName);

        SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.literal("Quest Completed"), Component.literal(title));

        MCUtilClient.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1, 2);
    }

    public static void displayDialog(String dialogIdentifier, int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID entity, String resourceLocation, Item item, NpcType npcType) {
        AbstractDialog dialogScreen = ScreenRegistry.getDialogScreen(dialogIdentifier);

        Minecraft.getInstance().setScreen(dialogScreen.modifyScreen(dialogID, text, optionSize, optionIntegers, optionStrings, entity, resourceLocation, item, npcType));
    }

    public static void closeDialog() {
        Minecraft.getInstance().setScreen(null);
    }

    public static void modifyTrackedQuests(Boolean addQuest, UserQuest userQuest) {
        //If addQuest is true and trackedQuest's goals are less than 5, can add another quest to tracked list
        if (addQuest) {
            var totalGoals = new AtomicInteger();
            trackedQuestList.forEach(quest -> totalGoals.addAndGet(quest.getQuestGoals().size()));
            if (totalGoals.get() < 5) {
                trackedQuestList.add(userQuest);
            }
        } else {
            trackedQuestList.removeIf(quest -> quest.getId().equals(userQuest.getId()));
        }
    }

    public static void updateQuestTracked(UserQuest userQuest) {
        NetworkClientHandler.trackedQuestList.forEach(quest -> {
            if (!userQuest.getId().equals(quest.getId())) return;

            quest.setQuestGoals(userQuest.getQuestGoals());
        });
    }

    public static void removeQuest(String id) {
        Timer.clearTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + id);
    }


    public static void setActiveQuestList(List<UserQuest> activeQuestList) {
        NetworkClientHandler.activeQuestList = activeQuestList;
    }


    public static void setAvailableEntityTypeForQuest(List<String> entityTypeForQuest, Map<String, String> entityTagMap) {
        for (String entityTypeString : entityTypeForQuest) {
            if (entityTypeString.isBlank()) return;
            ResourceLocation entityResourceLocation = new ResourceLocation(entityTypeString.strip());
            EntityType entityType = BuiltInRegistries.ENTITY_TYPE.get(entityResourceLocation);

            availableEntityTypeForQuest.add(entityType);
        }

        availableEntityTagForQuest = entityTagMap;
    }
}
