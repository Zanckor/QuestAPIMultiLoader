package net.zanckor.questapi.example.common.handler.questgoal;

import com.google.gson.Gson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.mod.common.questhandler.FabricAbstractGoal;

import java.io.File;
import java.io.IOException;

import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT;

@SuppressWarnings({"ConstantConditions", "rawtypes"})
public class CollectGoal extends FabricAbstractGoal {


    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        String questID = userQuest.getId();
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        //Check if the userQuest is null or doesn't match both id's
        if (userQuest == null || (!(questID.equals(userQuest.getId())))) return;

        updateData(player, file);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }


    // Method for updating quest data
    public void updateData(ServerPlayer player, File file) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        for (UserGoal questGoal : userQuest.getQuestGoals()) {
            if (!(questGoal.getType().equals(COLLECT.name()))) continue;

            int itemCount;
            String valueItem = questGoal.getTarget();
            Item itemTarget = BuiltInRegistries.ITEM.get(new ResourceLocation(valueItem));

            // Checks if the player's inventory contains the required item
            if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
                questGoal.setCurrentAmount(0);
            } else {
                int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                ItemStack item = player.getInventory().getItem(itemSlot);
                itemCount = item.getCount() > questGoal.getAmount() ? questGoal.getAmount() : item.getCount();

                questGoal.setCurrentAmount(itemCount);
            }

            GsonManager.writeJson(file, userQuest);
        }
    }

    // Method for removing items from player's inventory

    public static void removeItems(ServerPlayer player, UserGoal goalEnhanced) {
        if (!(goalEnhanced.getType().equals(COLLECT.name()))) return;

        String valueItem = goalEnhanced.getTarget();
        Item itemTarget = BuiltInRegistries.ITEM.get(new ResourceLocation(valueItem));

        int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());

        // Check if the item is not found in the player's inventory
        if (itemSlot < 0) return;
        player.getInventory().removeItem(itemSlot, goalEnhanced.getAmount());
    }


    // Method for completing a quest
    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        if (Util.isQuestCompleted(userQuest)) {
            removeItems(player, userGoal);
        }
    }

    @Override
    public Enum getGoalType() {
        return COLLECT;
    }
}
