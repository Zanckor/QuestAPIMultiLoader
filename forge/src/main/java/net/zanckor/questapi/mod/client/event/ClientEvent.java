package net.zanckor.questapi.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.api.file.npc.entity_type_tag.codec.EntityTypeTagDialog;
import net.zanckor.questapi.api.registry.ScreenRegistry;
import net.zanckor.questapi.api.screen.AbstractQuestLog;
import net.zanckor.questapi.mod.common.config.client.RendererConfig;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.util.Timer;
import org.joml.Quaternionf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static net.zanckor.questapi.ClientForgeQuestAPI.questMenu;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@SuppressWarnings("ConstantConditions, rawtypes")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvent {


    @SubscribeEvent
    public static void keyOpenScreen(InputEvent.Key e) throws IOException {
        if (questMenu != null && questMenu.isDown()) {
            AbstractQuestLog questLogScreen = ScreenRegistry.getQuestLogScreen(ScreenConfig.QUEST_LOG_SCREEN.get());

            Minecraft.getInstance().setScreen(questLogScreen.modifyScreen());
        }
    }

    @SubscribeEvent
    public static void loadHashMaps(ClientPlayerNetworkEvent.LoggingIn e) {
        ClientHandler.activeQuestList = new ArrayList<>();
    }


    @SubscribeEvent
    public static void renderNPCQuestMarker(RenderLivingEvent.Pre e) {
        Player player = Minecraft.getInstance().player;
        PoseStack poseStack = e.getPoseStack();
        Font font = Minecraft.getInstance().font;

        float distance = player.distanceTo(e.getEntity());
        double alphaMultiplier = Math.pow(8, ((75 - distance) / 120)) - 6.65;
        int flatColor = 0XFFFF00;

        int color = (int) (alphaMultiplier * 255.0F) << 24 | flatColor;

        if (player.distanceTo(e.getEntity()) < 15) {
            if (checkEntityTagIsValid(e.getEntity()) || checkEntityTypeIsValid(e.getEntity())) {
                poseStack.pushPose();

                poseStack.translate(0, e.getEntity().getBbHeight() + 1.25, 0);
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
        EntityType entityType = entity.getType();

        if (entity.getPersistentData().getBoolean("beingRenderedOnInventory")) return false;

        if (entity.getPersistentData().contains("availableForDialog")) {
            return entity.getPersistentData().getBoolean("availableForDialog");
        }

        if (ClientHandler.availableEntityTypeForQuest != null) {
            entity.getPersistentData().putBoolean("availableForDialog", ClientHandler.availableEntityTypeForQuest.contains(entityType));

            return ClientHandler.availableEntityTypeForQuest.contains(entityType);
        }

        return false;
    }

    public static boolean checkEntityTagIsValid(LivingEntity entity) {
        for (Map.Entry<String, String> entry : ClientHandler.availableEntityTagForQuest.entrySet()) {
            if (Timer.canUseWithCooldown(entity.getUUID(), "UPDATE_MARKER", RendererConfig.QUEST_MARK_UPDATE_COOLDOWN.get())) {
                Timer.updateCooldown(entity.getUUID(), "UPDATE_MARKER", RendererConfig.QUEST_MARK_UPDATE_COOLDOWN.get());

                // Get the entity's NBT
                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(entity);
                String value = entry.getValue();
                String targetEntityType = EntityType.getKey(entity.getType()).toString();

                // Get the list of dialogs associated with the entity type
                EntityTypeTagDialog entityTypeDialog = GsonManager.gson.fromJson(value, EntityTypeTagDialog.class);

                // Get the list of dialogs associated with the entity type, and check if the entity has the tags
                for (EntityTypeTagDialog.EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;

                    try {

                        EntityTypeTagDialog entityTypeConversation = (EntityTypeTagDialog) GsonManager.getJsonClass(value, EntityTypeTagDialog.class);
                        boolean isEntityType = entityTypeConversation.getEntity_type().contains(targetEntityType);
                        if (!isEntityType) continue;

                        switch (conditions.getLogic_gate()) {
                            // If the logic gate is OR, then if the entity has one of the tags, then it is available for the dialog
                            case OR -> {
                                for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                    if (entityNBT.get(nbt.getTag()) == null) {
                                        continue;
                                    }

                                    tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());

                                    entity.getPersistentData().putBoolean("availableForDialog", tagCompare);
                                    if (tagCompare) return true;
                                }
                            }

                            // If the logic gate is AND, then if the entity has all the tags, then it is available for the dialog
                            case AND -> {
                                boolean shouldAddMarker = false;

                                for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {

                                    if (entityNBT.get(nbt.getTag()) != null) {
                                        tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());
                                    } else {
                                        tagCompare = false;
                                    }

                                    shouldAddMarker = tagCompare;

                                    if (!tagCompare) break;
                                }

                                entity.getPersistentData().putBoolean("availableForDialog", shouldAddMarker);
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
            if (entity.getPersistentData().getBoolean("beingRenderedOnInventory")) return false;

            if (entity.getPersistentData().contains("availableForDialog")) {
                return entity.getPersistentData().getBoolean("availableForDialog");
            }
        }

        return false;
    }
}
