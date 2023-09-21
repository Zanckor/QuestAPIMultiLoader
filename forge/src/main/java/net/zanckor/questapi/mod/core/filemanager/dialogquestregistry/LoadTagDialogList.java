package net.zanckor.questapi.mod.core.filemanager.dialogquestregistry;


import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;
import net.zanckor.questapi.api.data.QuestDialogManager;
import net.zanckor.questapi.api.file.npc.entity_type_tag.codec.EntityTypeTagDialog;
import net.zanckor.questapi.util.GsonManager;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.compoundTag_List;
import static net.zanckor.questapi.mod.common.datapack.CompoundTagDialogJSONListener.datapackDialogPerCompoundTagList;

public class LoadTagDialogList {

    /**
     * Each time that server starts running, <code> registerNPCTagDialogList </code> is called to copy resource's files to minecraft folder.
     */

    static EntityTypeTagDialog entityTypeTagDialog;

    public static void registerNPCTagDialogList(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        if (compoundTag_List == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("npc/compound_tag_list", (file) -> {
            if (file.getPath().length() > 22) {
                String fileName = file.getPath().substring(22);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());
                if(!modid.equals(file.getNamespace())) return false;

                if (file.getPath().endsWith(".json")) {
                    read(resourceLocation, server);
                    write(entityTypeTagDialog, modid, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });

        QuestDialogManager.registerDialogByCompoundTag();
    }


    public static void registerDatapackTagDialogList(MinecraftServer server) throws IOException {
        if (compoundTag_List == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for(Map.Entry<String, JsonObject> entry : datapackDialogPerCompoundTagList.entrySet()){
            FileWriter writer = new FileWriter(String.valueOf(Path.of(compoundTag_List + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }


    private static void read(ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            if(server.getResourceManager().getResource(resourceLocation).isEmpty()) return;

            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            entityTypeTagDialog = GsonManager.gson.fromJson(new InputStreamReader(inputStream), EntityTypeTagDialog.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(EntityTypeTagDialog entityTypeDialog, String modid, String fileName) {
        try {
            if(entityTypeDialog == null) return;

            File file = new File(compoundTag_List.toFile(), modid + "." + fileName);
            GsonManager.writeJson(file, entityTypeDialog);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}