package net.zanckor.questapi.api.screenmanager;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class AbstractQuestTracked {
    public abstract void renderQuestTracked(PoseStack poseStack, int width, int height);
}
