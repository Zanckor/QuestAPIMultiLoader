package net.zanckor.questapi.api.registrymanager.enumquest;


import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestRequirement;

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
