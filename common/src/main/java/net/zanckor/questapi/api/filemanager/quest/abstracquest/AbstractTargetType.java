package net.zanckor.questapi.api.filemanager.quest.abstracquest;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;

public abstract class AbstractTargetType {

    /**
     * Class that returns as a human-readable text the quest target
     *
     * @param resourceLocationString Resource location of the target wanted to translate. Example: entity.minecraft.cow
     */

    public abstract MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1);
    public abstract String target(String resourceLocationString);
    public abstract void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString);
}
