package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.mod.common.util.MCUtil;

import java.io.IOException;

@SuppressWarnings("ConstantConditions")
public class ItemReward extends AbstractReward {

    /**
     * This method handles the reward distribution for a server quest.
     *
     * @param player      The player receiving the rewards.
     * @param serverQuest The server quest being completed.
     * @param rewardIndex The index of the reward being processed.
     * @throws IOException If an I/O error occurs during reward processing.
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {

        // Retrieve the item's tag and quantity from the server quest's rewards
        String valueItem = serverQuest.getRewards().get(rewardIndex).getTag();
        int quantity = serverQuest.getRewards().get(rewardIndex).getAmount();

        // Get the corresponding item from the item registry
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
        ItemStack baseStack = new ItemStack(item, 1);

        // Calculate how many stacks are needed to hold the entire quantity
        float stackCount = quantity / (float) baseStack.getMaxStackSize();

        // Check how many free inventory slots the player has
        int freeSlots = Util.getFreeSlots(player);

        // Distribute items to player's inventory or drop them if not enough space
        for (; stackCount >= 0; stackCount--) {
            int stackQuantity = Math.min(quantity, baseStack.getMaxStackSize());
            ItemStack stack = new ItemStack(item, stackQuantity);

            if (freeSlots >= stackCount) {
                player.addItem(stack);
            } else {
                player.drop(stack, false, false);
            }

            quantity -= baseStack.getMaxStackSize();
        }
    }
}