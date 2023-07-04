package net.zanckor.questapi.api.interfacemanager.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumQuestRequirement {
    AbstractQuestRequirement getRequirement();

    default void registerEnumQuestReq(Class enumClass) {
        EnumRegistry.registerQuestRequirement(enumClass);
    }
}
