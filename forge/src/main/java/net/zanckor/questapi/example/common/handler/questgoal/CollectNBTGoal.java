package net.zanckor.questapi.example.common.handler.questgoal;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.mod.common.questhandler.ForgeAbstractGoal;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumquest.EnumGoalType.COLLECT_WITH_NBT;

public class CollectNBTGoal extends ForgeAbstractGoal {
    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        String questID = userQuest.getId();
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null || (!(questID.equals(userQuest.getId())))) return;

        updateData(player, file);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }


    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        if (Util.isQuestCompleted(userQuest)) {
            removeItems(player, userGoal);
        }
    }

    public static void removeItems(ServerPlayer player, UserGoal goalEnhanced) {
        if (!(goalEnhanced.getType().equals(COLLECT_WITH_NBT.name()))) return;
        Inventory inventory = player.getInventory();

        String valueItem = goalEnhanced.getTarget();
        ItemStack itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem)).getDefaultInstance();
        List<Integer> itemSlotList = Util.findSlotMatchingItemStack(itemTarget, inventory);

        for (int i = 0; i < itemSlotList.size(); i++) {
            int itemSlot = itemSlotList.get(i);

            if (checkItemsNBT(goalEnhanced, player, itemSlot)) {
                inventory.removeItem(itemSlot, goalEnhanced.getAmount());
            }
        }
    }


    public void updateData(ServerPlayer player, File file) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        for (UserGoal goal : userQuest.getQuestGoals()) {
            if (!(goal.getType().contains(COLLECT_WITH_NBT.name()))) continue;


            int itemCount;
            String valueItem = goal.getTarget();
            ItemStack itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem)).getDefaultInstance();

            //Checks inventory's items with his nbt
            if (!player.getInventory().contains(itemTarget)) {
                goal.setCurrentAmount(0);
            } else {
                List<Integer> itemSlotList = Util.findSlotMatchingItemStack(itemTarget, player.getInventory());

                if (!itemSlotList.isEmpty() && checkItemsNBT(goal, player, itemSlotList)) {
                    ItemStack item = player.getInventory().getItem(itemSlotList.get(0));
                    itemCount = item.getCount() > goal.getAmount() ? goal.getAmount() : item.getCount();

                    goal.setCurrentAmount(itemCount);
                }
            }

            GsonManager.writeJson(file, userQuest);
        }
    }


    public static boolean checkItemsNBT(UserGoal goal, ServerPlayer player, List<Integer> itemSlotList) {
        Map<String, String> nbtTag = GsonManager.gson.fromJson(goal.getAdditionalClassData().toString(), new TypeToken<Map<String, String>>() {
        }.getType());


        for (Map.Entry<String, String> entry : nbtTag.entrySet()) {
            for (int i = 0; i < itemSlotList.size(); i++) {
                ItemStack itemStack = player.getInventory().getItem(itemSlotList.get(i));

                if (itemStack.getTag() == null || itemStack.getTag().get(entry.getKey()) == null ||
                        !(itemStack.getTag().get(entry.getKey()).getAsString().contains(entry.getValue()))) continue;

                return true;
            }
        }

        return false;
    }

    public static boolean checkItemsNBT(UserGoal goal, ServerPlayer player, int itemSlot) {
        Map<String, String> nbtTag = GsonManager.gson.fromJson(goal.getAdditionalClassData().toString(), new TypeToken<Map<String, String>>() {
        }.getType());


        for (Map.Entry<String, String> entry : nbtTag.entrySet()) {
            ItemStack itemStack = player.getInventory().getItem(itemSlot);

            if (itemStack.getTag() == null || itemStack.getTag().get(entry.getKey()) == null ||
                    !(itemStack.getTag().get(entry.getKey()).getAsString().contains(entry.getValue()))) continue;

            return true;

        }

        return false;
    }

    @Override
    public Enum getGoalType() {
        return COLLECT_WITH_NBT;
    }
}
