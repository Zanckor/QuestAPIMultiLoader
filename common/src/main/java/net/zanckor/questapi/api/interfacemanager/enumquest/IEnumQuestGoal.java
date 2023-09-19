package net.zanckor.questapi.api.interfacemanager.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumQuestGoal {
    AbstractGoal getQuest();

    default void registerEnumGoal(Class<?> enumClass) {
        EnumRegistry.registerQuestGoal(enumClass);
    }
}
