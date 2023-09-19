package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestReward;
import net.zanckor.questapi.example.common.handler.questreward.*;

public enum EnumQuestReward implements IEnumQuestReward {
    ITEM(new ItemReward()),
    COMMAND(new CommandReward()),
    QUEST(new QuestReward()),
    XP(new XpReward()), LEVEL(new XpReward()), POINTS(new XpReward()),
    LOOT_TABLE(new LootTableReward());

    final AbstractReward reward;

    EnumQuestReward(AbstractReward reward) {
        this.reward = reward;
        registerEnumReward(this.getClass());
    }

    @Override
    public AbstractReward getReward() {
        return reward;
    }
}
