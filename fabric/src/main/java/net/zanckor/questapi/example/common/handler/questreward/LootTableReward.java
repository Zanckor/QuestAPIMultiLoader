package net.zanckor.questapi.example.common.handler.questreward;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.zanckor.questapi.api.file.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumQuestReward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LootTableReward extends AbstractReward {

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
        String lootTableRL = serverQuest.getRewards().get(rewardIndex).getTag();
        int rolls = serverQuest.getRewards().get(rewardIndex).getAmount();
        MinecraftServer server = player.server;
        List<ItemStack> itemStackList = new ArrayList<>();

        ResourceLocation rl = new ResourceLocation(lootTableRL);
        LootTable lootTable = server.getLootData().getLootTable(rl);

        for (int actualRoll = 0; actualRoll < rolls; actualRoll++) {
            LootParams lootparams = (new LootParams.Builder((ServerLevel) player.level())).create(LootContextParamSets.EMPTY);
            itemStackList = lootTable.getRandomItems(lootparams);
        }

        for (ItemStack itemStack : itemStackList) {

            //If player's inventory has enough space, give to inventory, else drop it

            if (player.getInventory().getFreeSlot() > 0) {
                player.getInventory().add(itemStack);
            } else {
                player.drop(itemStack, false, false);
            }
        }
    }
}