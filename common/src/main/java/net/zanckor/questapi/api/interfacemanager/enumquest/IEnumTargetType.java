package net.zanckor.questapi.api.interfacemanager.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumTargetType {
    AbstractTargetType getTargetType();

    default void registerTargetType(Class<?> enumClass) {
        EnumRegistry.registerTargetType(enumClass);
    }
}
