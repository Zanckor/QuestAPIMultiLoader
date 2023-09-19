package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog;


import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogOption;
import net.zanckor.questapi.example.common.handler.dialogoption.DialogAddQuest;
import net.zanckor.questapi.example.common.handler.dialogoption.DialogCloseDialog;
import net.zanckor.questapi.example.common.handler.dialogoption.DialogOpenDialog;

public enum EnumDialogOption implements IEnumDialogOption {
    OPEN_DIALOG(new DialogOpenDialog()),
    CLOSE_DIALOG(new DialogCloseDialog()),
    ADD_QUEST(new DialogAddQuest());

    final AbstractDialogOption dialogOption;

    EnumDialogOption(AbstractDialogOption abstractDialogOption) {
        dialogOption = abstractDialogOption;
        registerEnumDialogOption(this.getClass());
    }

    @Override
    public AbstractDialogOption getDialogOption() {
        return dialogOption;
    }
}
