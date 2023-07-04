package net.zanckor.questapi.api.filemanager.quest.register;

import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import net.zanckor.questapi.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractGoal;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractReward;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogOption;
import net.zanckor.questapi.api.interfacemanager.enumdialog.IEnumDialogReq;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestGoal;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestRequirement;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumQuestReward;
import net.zanckor.questapi.api.interfacemanager.enumquest.IEnumTargetType;

import java.util.HashMap;

public class QuestTemplateRegistry {

    /**
     * This class stores quest types, reward types, requirement types... as temporary data to improve access time.
     * You should use these methods to add your own quests.
     */


    private static HashMap<Enum, AbstractGoal> quest_goal = new HashMap<>();
    private static HashMap<Enum, AbstractReward> quest_reward = new HashMap<>();
    private static HashMap<Enum, AbstractQuestRequirement> quest_requirement = new HashMap<>();
    private static HashMap<Enum, AbstractDialogRequirement> dialog_requirement = new HashMap<>();
    private static HashMap<Enum, AbstractDialogOption> dialog_template = new HashMap<>();

    private static HashMap<Enum, AbstractTargetType> target_type = new HashMap<>();

    public static void registerQuest(IEnumQuestGoal key) {
        quest_goal.put((Enum) key, key.getQuest());
    }

    public static void registerDialogOption(IEnumDialogOption key) {
        dialog_template.put((Enum) key, key.getDialogOption());
    }

    public static void registerReward(IEnumQuestReward key) {
        quest_reward.put((Enum) key, key.getReward());
    }

    public static void registerQuestRequirement(IEnumQuestRequirement key) {
        quest_requirement.put((Enum) key, key.getRequirement());
    }

    public static void registerDialogRequirement(IEnumDialogReq key) {
        dialog_requirement.put((Enum) key, key.getDialogRequirement());
    }

    public static void registerTargetType(IEnumTargetType key) {
        target_type.put((Enum) key, key.getTargetType());
    }


    public static AbstractGoal getQuestTemplate(Enum key) {
        try {
            return quest_goal.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect quest key: " + key);
        }
    }

    public static HashMap<Enum, AbstractGoal> getAllGoals() {
        return quest_goal;
    }

    public static AbstractDialogOption getDialogTemplate(Enum key) {
        try {
            return dialog_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect quest key: " + key);
        }
    }

    public static AbstractReward getQuestReward(Enum key) {
        try {
            return quest_reward.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect reward key: " + key);
        }
    }

    public static AbstractQuestRequirement getQuestRequirement(Enum key) {
        try {
            return quest_requirement.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect requirement key: " + key);
        }
    }

    public static AbstractDialogRequirement getDialogRequirement(Enum key) {
        try {
            return dialog_requirement.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect requirement key: " + key);
        }
    }

    public static AbstractTargetType getTranslatableTargetType(Enum key) {
        return target_type.getOrDefault(key, new DefaultTargetType());
    }
}