package net.zanckor.questapi.api.registrymanager.enumdialog;

import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogReq;

public enum EnumDialogReq implements IEnumDialogReq {
    QUEST(new QuestRequirement()),
    DIALOG(new DialogRequirement());


    AbstractDialogRequirement dialogRequirement;
    EnumDialogReq(AbstractDialogRequirement abstractDialogRequirement) {
        dialogRequirement = abstractDialogRequirement;
        registerEnumDialogReq(this.getClass());
    }

    @Override
    public AbstractDialogRequirement getDialogRequirement() {
        return dialogRequirement;
    }
}
