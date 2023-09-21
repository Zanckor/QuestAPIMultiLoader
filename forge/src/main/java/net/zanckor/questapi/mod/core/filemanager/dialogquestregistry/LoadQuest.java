package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;
import net.zanckor.questapi.api.file.quest.codec.server.ServerQuest;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.datapack.QuestJSONListener;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.serverQuests;

public class LoadQuest {

    /**
     * Each time that server starts running, <code> registerquest </code> is called to copy resource's quest files to minecraft folder.
     */


    static ServerQuest playerQuest;

    public static void registerQuest(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        if (serverQuests == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("quest", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(6);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());
                if (!modid.equals(file.getNamespace())) return false;

                if (file.getPath().endsWith(".json")) {
                    read(resourceLocation, resourceManager);
                    write(playerQuest, fileName, modid);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }

    public static void registerDatapackQuest(MinecraftServer server) throws IOException {
        if (serverQuests == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for (Map.Entry<String, JsonObject> entry : QuestJSONListener.datapackQuestList.entrySet()) {
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverQuests + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }

    private static void read(ResourceLocation resourceLocation, ResourceManager resourceManager) {
        try {
            if (resourceManager.getResource(resourceLocation).isEmpty()) return;

            InputStream inputStream = resourceManager.getResource(resourceLocation).get().open();
            playerQuest = GsonManager.gson.fromJson(new InputStreamReader(inputStream), ServerQuest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(ServerQuest serverQuest, String fileName, String identifier) {
        try {
            if (serverQuest == null) return;

            serverQuest.setId(identifier + "." + fileName.substring(0, fileName.length() - 5));
            File file = new File(serverQuests.toFile(), identifier + "." + fileName);

            GsonManager.writeJson(file, serverQuest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}