package net.zanckor.questapi.mod.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.dialog.codec.ReadDialog;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerQuest;
import net.zanckor.questapi.api.filemanager.quest.codec.server.ServerRequirement;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Util;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.*;

@SuppressWarnings("ConstantConditions, rawtypes, unused")
@EventSubscriber(side = Side.SERVER)
public class MCUtil {

    public static Entity getEntityLookinAt(Entity rayTraceEntity, double distance) {
        float playerRotX = rayTraceEntity.getXRot();
        float playerRotY = rayTraceEntity.getYRot();
        Vec3 startPos = rayTraceEntity.getEyePosition();
        float f2 = Mth.cos(-playerRotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-playerRotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-playerRotX * ((float) Math.PI / 180F));
        float additionY = Mth.sin(-playerRotX * ((float) Math.PI / 180F));
        float additionX = f3 * f4;
        float additionZ = f2 * f4;
        double d0 = distance;
        Vec3 endVec = startPos.add(((double) additionX * d0), ((double) additionY * d0), ((double) additionZ * d0));

        AABB startEndBox = new AABB(startPos, endVec);
        Entity entity = null;
        for (Entity entity1 : rayTraceEntity.level().getEntities(rayTraceEntity, startEndBox, (val) -> true)) {
            AABB aabb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = aabb.clip(startPos, endVec);
            if (aabb.contains(startPos)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    startPos = optional.orElse(startPos);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3 vec31 = optional.get();
                double d1 = startPos.distanceToSqr(vec31);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == rayTraceEntity.getRootVehicle()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            startPos = vec31;
                        }
                    } else {
                        entity = entity1;
                        startPos = vec31;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    public static void addQuest(Player player, String questID) throws IOException {
        String quest = questID + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (hasQuest(quest, userFolder)) {
            for (File file : serverQuests.toFile().listFiles()) {
                if (!(file.getName().equals(quest))) continue;
                Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
                ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

                //Checks if player has all requirements
                for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                    ServerRequirement serverRequirement = serverQuest.getRequirements().get(requirementIndex);
                    String requirementType = serverRequirement.getType() != null ? serverRequirement.getType() : "NONE";
                    Enum<?> questRequirementEnum = EnumRegistry.getEnum(requirementType, EnumRegistry.getDialogRequirement());

                    AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                    if (!requirement.handler(player, serverQuest, requirementIndex)) {
                        return;
                    }
                }

                Util.createQuest(serverQuest, player, path);
                QuestDialogManager.registerQuestByID(questID, path);

                SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
                return;
            }
        }
    }

    public static BlockHitResult getHitResult(Level level, Entity player, float multiplier) {
        //Base values
        float xRot = player.getXRot();
        float yRot = player.getYRot();
        Vec3 eyePos = player.getEyePosition();

        //Getting yRotation cos and sin
        float yRotCos = Mth.cos(-yRot * ((float) Math.PI / 180F) - (float) Math.PI);
        float yRotSin = Mth.sin(-yRot * ((float) Math.PI / 180F) - (float) Math.PI);

        //Formula to convert float to degrees
        float xCosDegrees = -Mth.cos((float) -Math.toDegrees(xRot));
        float xSinDegrees = Mth.sin((float) -Math.toDegrees(xRot));
        float yCosRotation = yRotCos * xCosDegrees;
        float ySinRotation = yRotSin * xCosDegrees;

        //Distance in blocks, multiplier is applied to player reach distance
        double viewDistance = 8 * multiplier;

        Vec3 lookingVector = eyePos.add((double) ySinRotation * viewDistance, (double) xSinDegrees * viewDistance, (double) yCosRotation * viewDistance);
        return level.clip(new ClipContext(eyePos, lookingVector, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }


    public static void writeDialogRead(Player player, int dialogID) throws IOException {
        String globalDialog = QuestDialogManager.currentGlobalDialog.get(player);


        Path userFolder = Paths.get(CommonMain.playerData.toString(), player.getUUID().toString());

        Path path = Paths.get(CommonMain.getReadDialogs(userFolder).toString(), File.separator, "dialog_read.json");
        File file = path.toFile();

        ReadDialog.GlobalID dialog = file.exists() ? (ReadDialog.GlobalID) GsonManager.getJsonClass(file, ReadDialog.GlobalID.class) : null;

        List<ReadDialog.DialogID> dialogIDList;
        if (dialog == null) {
            dialogIDList = new ArrayList<>();
        } else {
            dialogIDList = dialog.getDialog_id();

            for (ReadDialog.DialogID id : dialogIDList) {
                if (id.getDialog_id() == dialogID) {
                    return;
                }
            }
        }

        dialogIDList.add(new ReadDialog.DialogID(dialogID));
        ReadDialog.GlobalID globalIDClass = new ReadDialog.GlobalID(globalDialog, dialogIDList);

        GsonManager.writeJson(file, globalIDClass);
    }

    public static boolean isReadDialog(Player player, int dialogID) throws IOException {
        Path userFolder = Paths.get(CommonMain.playerData.toString(), player.getUUID().toString());

        Path path = Paths.get(CommonMain.getReadDialogs(userFolder).toString(), File.separator, "dialog_read.json");
        File file = path.toFile();
        ReadDialog.GlobalID dialog = file.exists() ? (ReadDialog.GlobalID) GsonManager.getJsonClass(file, ReadDialog.GlobalID.class) : null;
        List<ReadDialog.DialogID> dialogIDList;

        if (dialog != null) {
            dialogIDList = dialog.getDialog_id();

            for (ReadDialog.DialogID id : dialogIDList) {
                if (id.getDialog_id() == dialogID) {
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean hasQuest(String quest, Path userFolder) {
        return Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getFailedQuest(userFolder).toString(), quest));
    }

    public static void moveFileToCompletedFolder(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        String questName = userQuest.getId() + ".json";

        Files.deleteIfExists(Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
        Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            Enum goalEnum = EnumRegistry.getEnum(userQuest.getQuestGoals().get(indexGoals).getType(), EnumRegistry.getQuestGoal());

            QuestDialogManager.movePathQuest(userQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), goalEnum);
        }
    }

    public static void moveFileToUncompletedFolder(Path uncompletedQuestFolder, File file, UserQuest userQuest, Enum goalEnum) throws IOException {
        Path uncompletedPath = Paths.get(uncompletedQuestFolder.toString(), file.getName());

        if (file.exists()) {
            Files.move(file.toPath(), uncompletedPath);
        }
        QuestDialogManager.movePathQuest(userQuest.getId(), uncompletedPath, goalEnum);
    }

    public static Entity getEntityByUUID(ServerLevel level, UUID uuid) {
        for (Entity entity : level.getAllEntities()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }

        return null;
    }

    public static List<Integer> findSlotMatchingItemStack(ItemStack itemStack, Inventory inventory) {
        List<Integer> slots = new ArrayList<>();

        for (int itemSlot = 0; itemSlot < inventory.items.size(); ++itemSlot) {
            if (!inventory.items.get(itemSlot).isEmpty() && ItemStack.isSameItem(itemStack, inventory.items.get(itemSlot))) {
                slots.add(itemSlot);
            }
        }

        return slots;
    }


    public static int randomBetween(double min, double max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}