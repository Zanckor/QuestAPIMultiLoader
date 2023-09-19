package net.zanckor.questapi.example.common.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;

public class NPCRenderer extends VillagerRenderer {
    public NPCRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
