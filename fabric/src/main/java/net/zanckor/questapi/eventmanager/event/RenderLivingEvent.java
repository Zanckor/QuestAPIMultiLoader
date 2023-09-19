package net.zanckor.questapi.eventmanager.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

public final class RenderLivingEvent {

    public static final Event<AfterRenderLivingEntity> AFTER_RENDER_LIVING_ENTITY = EventFactory.createArrayBacked(AfterRenderLivingEntity.class,
            callbacks -> ((entity, f, g, poseStack, multiBufferSource, i) -> {
                for (AfterRenderLivingEntity callback : callbacks) {
                    callback.afterRenderLivingEntity(entity, f, g, poseStack, multiBufferSource, i);
                }
            }));


    @FunctionalInterface
    public interface AfterRenderLivingEntity {
        /**
         * Called after an entity as being rendered
         */

        void afterRenderLivingEntity(Entity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i);
    }


    private RenderLivingEvent() {

    }
}
