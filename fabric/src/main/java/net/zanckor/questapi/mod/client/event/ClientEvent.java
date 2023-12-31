package net.zanckor.questapi.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.file.npc.entity_type_tag.codec.EntityTypeTagDialog;
import net.zanckor.questapi.api.registry.ScreenRegistry;
import net.zanckor.questapi.api.screen.AbstractQuestLog;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.RenderLivingEvent;
import net.zanckor.questapi.mod.common.config.client.RendererConfig;
import net.zanckor.questapi.mod.common.network.handler.NetworkClientHandler;
import net.zanckor.questapi.mod.core.data.IEntityData;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import org.joml.Quaternionf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static net.zanckor.questapi.ClientHandler.questMenu;
import static net.zanckor.questapi.mod.common.config.client.ScreenConfig.QUEST_LOG_SCREEN;

@EventSubscriber
public class ClientEvent {

    @SubscribeEvent(event = ClientTickEvents.EndTick.class)
    public static void tickEvent(Minecraft client) throws IOException {
        keyOpenScreen(client);
    }

    private static void keyOpenScreen(Minecraft client) throws IOException {
        if (questMenu.isDown()) {
            AbstractQuestLog questLogScreen = ScreenRegistry.getQuestLogScreen(QUEST_LOG_SCREEN);

            client.setScreen(questLogScreen.modifyScreen());
        }
    }

    @SubscribeEvent(event = ClientLoginConnectionEvents.Init.class)
    public static void loadHashMaps(ClientHandshakePacketListenerImpl handler, Minecraft client) {
        NetworkClientHandler.activeQuestList = new ArrayList<>();
    }


    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent(event = RenderLivingEvent.AfterRenderLivingEntity.class)
    public static void renderNPCQuestMarker(Entity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (!(entity instanceof LivingEntity)) return;

        Player player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;

        float distance = player.distanceTo(entity);
        double alphaMultiplier = Math.pow(8, ((75 - distance) / 120)) - 6.65;
        int flatColor = 0XFFFF00;

        int color = (int) (alphaMultiplier * 255.0F) << 24 | flatColor;

        if (player.distanceTo(entity) < 15) {
            if (checkEntityTagIsValid((LivingEntity) entity) || checkEntityTypeIsValid((LivingEntity) entity)) {
                poseStack.pushPose();

                poseStack.translate(0, entity.getBbHeight() + 1.25, 0);
                poseStack.scale(0.15f, 0.125f, 0.15f);
                poseStack.mulPose(new Quaternionf().rotateXYZ((float) Math.toRadians(180), (float) Math.toRadians(player.getYHeadRot() + 180), 0));

                font.drawInBatch(Component.literal("!"),
                        0, 0, color,
                        false, poseStack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(),
                        Font.DisplayMode.SEE_THROUGH, 0, 0);

                poseStack.popPose();
            }
        }
    }


    public static boolean checkEntityTypeIsValid(LivingEntity entity) {
        EntityType<?> entityType = entity.getType();
        CompoundTag tag = ((IEntityData) entity).getPersistentData();

        if (tag.getBoolean("beingRenderedOnInventory")) return false;

        if (tag.contains("availableForDialog")) {
            return tag.getBoolean("availableForDialog");
        }

        if (NetworkClientHandler.availableEntityTypeForQuest != null) {
            tag.putBoolean("availableForDialog", NetworkClientHandler.availableEntityTypeForQuest.contains(entityType));

            return NetworkClientHandler.availableEntityTypeForQuest.contains(entityType);
        }

        return false;
    }


    @SuppressWarnings("ConstantConditions")
    public static boolean checkEntityTagIsValid(LivingEntity entity) {
        CompoundTag tag = ((IEntityData) entity).getPersistentData();

        // Check if the entity has the tag that is available for the dialog
        for (Map.Entry<String, String> entry : NetworkClientHandler.availableEntityTagForQuest.entrySet()) {
            if (Timer.canUseWithCooldown(entity.getUUID(), "UPDATE_MARKER", Float.parseFloat(RendererConfig.QUEST_MARK_UPDATE_COOLDOWN))) {
                Timer.updateCooldown(entity.getUUID(), "UPDATE_MARKER", Float.parseFloat(RendererConfig.QUEST_MARK_UPDATE_COOLDOWN));

                // Get the entity's NBT
                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(entity);
                String value = entry.getValue();
                String targetEntityType = EntityType.getKey(entity.getType()).toString();

                // Get the list of dialogs associated with the entity type
                EntityTypeTagDialog entityTypeDialog = GsonManager.gson.fromJson(value, EntityTypeTagDialog.class);

                // Get the list of dialogs associated with the entity type, and check if the entity has the tags
                for (EntityTypeTagDialog.EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;
                    boolean shouldAddMarker;

                    try {

                        EntityTypeTagDialog entityTypeConversation = (EntityTypeTagDialog) GsonManager.getJsonClass(value, EntityTypeTagDialog.class);
                        boolean isEntityType = entityTypeConversation.getEntity_type().contains(targetEntityType);
                        if (!isEntityType) {
                            shouldAddMarker = false;
                            continue;
                        }

                        switch (conditions.getLogic_gate()) {
                            // If the logic gate is OR, then if the entity has one of the tags, then it is available for the dialog
                            case OR -> {
                                for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                    if (entityNBT.get(nbt.getTag()) == null) {
                                        continue;
                                    }

                                    tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());

                                    tag.putBoolean("availableForDialog", tagCompare);
                                    if (tagCompare) return true;
                                }
                            }

                            // If the logic gate is AND, then if the entity has all the tags, then it is available for the dialog
                            case AND -> {
                                shouldAddMarker = false;

                                for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {

                                    if (entityNBT.get(nbt.getTag()) != null) {
                                        tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());
                                    } else {
                                        tagCompare = false;
                                    }

                                    shouldAddMarker = tagCompare;

                                    if (!tagCompare) break;
                                }

                                tag.putBoolean("availableForDialog", shouldAddMarker);
                                if (shouldAddMarker) {
                                    return true;
                                }

                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // If the entity has the tag, then it is available for the dialog
            if (tag.getBoolean("beingRenderedOnInventory")) return false;

            if (tag.contains("availableForDialog")) {
                return tag.getBoolean("availableForDialog");
            }
        }

        return false;
    }
}
