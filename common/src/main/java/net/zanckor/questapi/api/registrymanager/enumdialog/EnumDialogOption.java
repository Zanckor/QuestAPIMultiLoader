package net.zanckor.questapi.api.registrymanager.enumdialog;


import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogOption;

public enum EnumDialogOption implements IEnumDialogOption {
    OPEN_DIALOG(new DialogOpenDialog()),
    CLOSE_DIALOG(new DialogCloseDialog()),
    ADD_QUEST(new DialogAddQuest());

    AbstractDialogOption dialogOption;

    EnumDialogOption(AbstractDialogOption abstractDialogOption) {
        dialogOption = abstractDialogOption;
        registerEnumDialogOption(this.getClass());
    }

    @Override
    public AbstractDialogOption getDialogOption() {
        return dialogOption;
    }
}
