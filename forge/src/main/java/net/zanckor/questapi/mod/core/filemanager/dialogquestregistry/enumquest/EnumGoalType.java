package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest;

import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestGoal;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumTargetType;
import net.zanckor.questapi.example.common.handler.questgoal.*;
import net.zanckor.questapi.example.common.handler.targettype.EntityTargetType;
import net.zanckor.questapi.example.common.handler.targettype.ItemTargetType;
import net.zanckor.questapi.example.common.handler.targettype.MoveToTargetType;
import net.zanckor.questapi.example.common.handler.targettype.XPTargetType;

public enum EnumGoalType implements IEnumQuestGoal {
    INTERACT_ENTITY(new InteractEntityGoal()),
    INTERACT_SPECIFIC_ENTITY(new InteractSpecificEntityGoal()),
    KILL(new KillGoal()),
    MOVE_TO(new MoveToGoal()),
    COLLECT(new CollectGoal()),
    COLLECT_WITH_NBT(new CollectNBTGoal()),
    XP(new XpGoal());


    final AbstractGoal goal;

    EnumGoalType(AbstractGoal abstractGoal) {
        this.goal = abstractGoal;
        registerEnumGoal(this.getClass());
    }

    @Override
    public AbstractGoal getQuest() {
        return goal;
    }


    public enum EnumTargetType implements IEnumTargetType {
        TARGET_TYPE_INTERACT_ENTITY(new EntityTargetType()),
        TARGET_TYPE_INTERACT_SPECIFIC_ENTITY(new EntityTargetType()),
        TARGET_TYPE_KILL(new EntityTargetType()),
        TARGET_TYPE_MOVE_TO(new MoveToTargetType()),
        TARGET_TYPE_COLLECT(new ItemTargetType()),
        TARGET_TYPE_COLLECT_WITH_NBT(new ItemTargetType()),
        TARGET_TYPE_XP(new XPTargetType());

        final AbstractTargetType targetType;

        EnumTargetType(AbstractTargetType targetType) {
            this.targetType = targetType;
            registerTargetType(this.getClass());
        }

        @Override
        public AbstractTargetType getTargetType() {
            return targetType;
        }
    }
}
