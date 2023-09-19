package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry;

import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.CommonMain;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.CommonMain.playerData;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FolderManager {

    /**
     * Each time that server starts <code> serverFolderManager </code> is called to create base folders
     * Each time a player joins the server, <code> playerFolderManager </code> is called to create his data folders
     */

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) {
        Path serverDirectory = e.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath();

        createAPIFolder(serverDirectory);
    }

    public static void createAPIFolder(Path serverDirectory) {
        File questApi = new File(serverDirectory.toString(), "quest-api");
        File playerData = new File(questApi.toString(), "player-data");
        File serverQuests = new File(questApi.toString(), "server-quests");
        File serverDialogs = new File(questApi.toString(), "server-dialogs");
        File serverNPC = new File(questApi.toString(), "server-npc");
        File entity_type_list = new File(serverNPC.toString(), "entity_type_list");
        File compoundTag_list = new File(serverNPC.toString(), "compound_tag_list");

        File[] paths = {questApi, playerData, serverQuests, serverDialogs, serverNPC, entity_type_list, compoundTag_list};

        for (File file : paths) {
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        CommonMain.serverDirectory = serverDirectory;
        CommonMain.questApi = questApi.toPath();
        CommonMain.playerData = playerData.toPath();
        CommonMain.serverQuests = serverQuests.toPath();
        CommonMain.serverDialogs = serverDialogs.toPath();
        CommonMain.serverNPC = serverNPC.toPath();
        CommonMain.entity_type_list = entity_type_list.toPath();
        CommonMain.compoundTag_List = compoundTag_list.toPath();
    }


    @SubscribeEvent
    public static void playerFolderManager(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().level().isClientSide || e.getEntity() == null) return;

        Path userFolder = Paths.get(playerData.toString(), e.getEntity().getUUID().toString());

        if (!userFolder.toFile().exists()) {
            CommonMain.getActiveQuest(userFolder).toFile().mkdirs();
            CommonMain.getCompletedQuest(userFolder).toFile().mkdirs();
            CommonMain.getFailedQuest(userFolder).toFile().mkdirs();
            CommonMain.getReadDialogs(userFolder).toFile().mkdirs();
        }
    }
}
