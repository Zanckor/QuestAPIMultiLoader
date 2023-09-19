package net.zanckor.questapi.mod.common.datapack;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.zanckor.questapi.commonutil.GsonManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class CompoundTagDialogJSONListener implements IdentifiableResourceReloadListener {
    public static HashMap<String, JsonObject> datapackDialogPerCompoundTagList = new HashMap<>();

    public void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(this);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(MOD_ID, "questapi/npc/compound_tag_list");
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller, @NotNull ProfilerFiller profilerFiller2, @NotNull Executor executor, @NotNull Executor executor2) {
        LOG.info("Loaded list of dialogs loaded compound tag entity type datapack");

        return CompletableFuture.runAsync(() -> {

            resourceManager.listResources(MOD_ID, resourceLocation -> resourceLocation.getPath().endsWith(".json"))
                    .forEach((resourceLocation, resource) -> {
                        try {
                            JsonObject obj = GsonManager.gson.fromJson(resource.openAsReader(), JsonObject.class);
                            if (obj.get("id") != null && obj.get("entity_type") != null) {
                                String questId = "." + obj.get("id").toString().substring(1, obj.get("id").toString().length() - 1);
                                Path path = Path.of(resourceLocation.getNamespace() + questId + ".json");

                                datapackDialogPerCompoundTagList.put(path.toString(), obj);
                            }
                        } catch (IOException e) {
                            LOG.error("Error loading CompoundTagDialogJson for " + resourceLocation.getNamespace());
                            throw new RuntimeException(e);
                        }
                    });
        });
    }
}