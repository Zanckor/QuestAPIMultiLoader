package net.zanckor.questapi.api.filemanager.quest.codec.server;

import net.zanckor.questapi.api.filemanager.FileAbstract;

import java.util.List;

public class ServerQuest extends FileAbstract {
    private String id;
    private String title;
    private boolean hasTimeLimit;
    private int timeLimitInSeconds;
    private List<ServerGoal> goals;
    private List<ServerReward> rewards;
    private List<ServerRequirement> requirements;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean hasTimeLimit() {
        return hasTimeLimit;
    }

    public void setHasTimeLimit(boolean hasTimeLimit) {
        this.hasTimeLimit = hasTimeLimit;
    }

    public int getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public void setTimeLimitInSeconds(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
    }

    public List<ServerGoal> getGoalList() {
        return goals;
    }

    public void setGoalList(List<ServerGoal> goals) {
        this.goals = goals;
    }

    public List<ServerReward> getRewards() {
        return rewards;
    }

    public void setRewards(List<ServerReward> ServerRewards) {
        this.rewards = ServerRewards;
    }

    public List<ServerRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<ServerRequirement> ServerRequirements) {
        this.requirements = ServerRequirements;
    }
}

