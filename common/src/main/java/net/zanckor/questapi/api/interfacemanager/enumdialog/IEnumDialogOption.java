package net.zanckor.questapi.api.interfacemanager.enumdialog;

import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;

public interface IEnumDialogOption {
    AbstractDialogOption getDialogOption();

    default void registerEnumDialogOption(Class<?> enumClass) {
        EnumRegistry.registerDialogOption(enumClass);
    }
}
