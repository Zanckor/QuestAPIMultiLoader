package net.zanckor.questapi.api.interfacemanager.enumdialog;

import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumDialogReq {
    AbstractDialogRequirement getDialogRequirement();

    default void registerEnumDialogReq(Class<?> enumClass) {
        EnumRegistry.registerDialogRequirement(enumClass);
    }
}
