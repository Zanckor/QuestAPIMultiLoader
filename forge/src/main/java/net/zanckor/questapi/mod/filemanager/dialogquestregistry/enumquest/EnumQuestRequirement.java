package net.zanckor.questapi.mod.filemanager.dialogquestregistry.enumquest;


import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestRequirement;
import net.zanckor.questapi.example.common.handler.questrequirement.XpRequirement;

public enum EnumQuestRequirement implements IEnumQuestRequirement {
    XP(new XpRequirement());

    AbstractQuestRequirement questRequirement;

    EnumQuestRequirement(AbstractQuestRequirement abstractQuestRequirement) {
        questRequirement = abstractQuestRequirement;
        registerEnumQuestReq(this.getClass());
    }

    @Override
    public AbstractQuestRequirement getRequirement() {
        return questRequirement;
    }
}
