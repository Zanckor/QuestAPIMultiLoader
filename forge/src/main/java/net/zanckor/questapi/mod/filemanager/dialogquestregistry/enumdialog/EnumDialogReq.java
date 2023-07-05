package net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumdialog;

import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogReq;
import net.zanckor.questapi.example.common.handler.dialogrequirement.DialogRequirement;
import net.zanckor.questapi.example.common.handler.dialogrequirement.QuestRequirement;

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
