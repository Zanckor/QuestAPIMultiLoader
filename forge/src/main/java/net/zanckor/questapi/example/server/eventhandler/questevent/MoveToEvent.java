package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.api.datamanager.QuestDialogManager;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.commonutil.GsonManager;
import net.zanckor.questapi.commonutil.Mathematic;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static net.minecraft.core.Direction.Axis.*;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.MOVE_TO;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MoveToEvent {

    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player == null || e.side.isClient() || e.player.getServer().getTickCount() % 20 != 0) return;

        List<Path> moveToQuests = QuestDialogManager.getQuestTypeLocation(MOVE_TO);

        if (moveToQuests != null) {
            for (Path path : moveToQuests) {
                UserQuest playerQuest = (UserQuest) GsonManager.getJsonClass(path.toFile(), UserQuest.class);
                if (playerQuest == null || playerQuest.isCompleted()) continue;

                moveTo(playerQuest, (ServerPlayer) e.player);
            }
        }
    }

    @SuppressWarnings("all")
    public static void moveTo(UserQuest userQuest, ServerPlayer player) throws IOException {
        BlockPos targetPos = null;

        //Checks per each goal if there's any MOVE_TO goal,
        //Then, add a new position to targetPos via additionalListData field from json
        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
            List<Double> additionalListData = (List<Double>) questGoal.getAdditionalListData();

            if (!(questGoal.getType().equals(MOVE_TO.toString())) || additionalListData == null) continue;
            Vec3i vec3Coord = new Vec3i(additionalListData.get(0).intValue(), additionalListData.get(1).intValue(), additionalListData.get(2).intValue());

            targetPos = new BlockPos(vec3Coord);
        }

        //If player coords are in a range of 10 blocks to target coord, executes questHandler
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (targetPos != null && Mathematic.numberBetween(
                playerCoord.x, targetPos.get(X) - 10, targetPos.get(X) + 10) &&
                Mathematic.numberBetween(playerCoord.y, targetPos.get(Y) - 10, targetPos.get(Y) + 10) &&
                Mathematic.numberBetween(playerCoord.z, targetPos.get(Z) - 10, targetPos.get(Z) + 10)) {

            ServerHandler.questHandler(MOVE_TO, player, null);
        }
    }
}
