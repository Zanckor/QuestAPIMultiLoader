package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.IOException;

@SuppressWarnings("ConstantConditions")
public class ItemReward extends AbstractReward {

    /**
     * Type of reward, gives player whatever item is set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        String valueItem = serverQuest.getRewards().get(rewardIndex).getTag();
        int quantity = serverQuest.getRewards().get(rewardIndex).getAmount();

        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(valueItem));
        ItemStack stack = new ItemStack(item, quantity);

        //If player's inventory has enough space, give to inventory, else drop it
        int stackCount = quantity / stack.getMaxStackSize();

        if (player.getInventory().getFreeSlot() > stackCount) {
            player.addItem(stack);
        } else {
            player.drop(stack, false, false);
        }
    }
}