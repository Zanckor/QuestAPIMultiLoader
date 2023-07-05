package net.zanckor.questapi.api.screenmanager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public abstract class AbstractQuestTracked {
    public abstract void renderQuestTracked(GuiGraphics graphics, int width, int height);
}
