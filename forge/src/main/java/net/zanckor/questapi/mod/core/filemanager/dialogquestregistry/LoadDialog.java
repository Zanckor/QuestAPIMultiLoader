package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry;


import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;
import net.zanckor.questapi.api.file.dialog.codec.Conversation;
import net.zanckor.questapi.util.GsonManager;
import net.zanckor.questapi.mod.common.datapack.DialogJSONListener;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.serverDialogs;

public class LoadDialog {

    /**
     * Each time that server starts running, <code> registerDialog </code> is called to copy resource's dialog files to minecraft folder.
     */

    static Conversation dialogTemplate;

    public static void registerDialog(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        if (serverDialogs == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("dialog", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(7);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());
                if(!modid.equals(file.getNamespace())) return false;

                if (file.getPath().endsWith(".json")) {
                    read(resourceLocation, server);
                    write(dialogTemplate, modid, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }


    public static void registerDatapackDialog(MinecraftServer server) throws IOException {
        if (serverDialogs == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for(Map.Entry<String, JsonObject> entry : DialogJSONListener.datapackDialogList.entrySet()){
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverDialogs + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }

    private static void read(ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            if(server.getResourceManager().getResource(resourceLocation).isEmpty()) return;

            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            dialogTemplate = GsonManager.gson.fromJson(new InputStreamReader(inputStream), Conversation.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Conversation conversation, String identifier, String fileName) {
        try {
            if(conversation == null) return;

            File file = new File(serverDialogs.toFile(), identifier + "." + fileName);

            if (conversation.getIdentifier() == null || conversation.getIdentifier().isEmpty()) {
                conversation.setIdentifier(identifier);
            }

            conversation.setConversationID(identifier + "." + fileName.substring(0, fileName.length() - 5));
            GsonManager.writeJson(file, conversation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}