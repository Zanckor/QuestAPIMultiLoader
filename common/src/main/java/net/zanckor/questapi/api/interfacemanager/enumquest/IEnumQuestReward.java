package net.zanckor.questapi.api.interfacemanager.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumQuestReward {
    AbstractReward getReward();

    default void registerEnumReward(Class<?> enumClass) {
        EnumRegistry.registerQuestReward(enumClass);
    }
}
