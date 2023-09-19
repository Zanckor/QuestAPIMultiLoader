package net.zanckor.questapi.mod.common.datapack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.zanckor.questapi.commonutil.GsonManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.Constants.LOG;

public class QuestJSONListener extends SimpleJsonResourceReloadListener {
    public static HashMap<String, JsonObject> datapackQuestList = new HashMap<>();

    public QuestJSONListener(Gson gson, String name) {
        super(gson, name);
    }

    public static void register(AddReloadListenerEvent e) {
        e.addListener(new QuestJSONListener(GsonManager.gson, "questapi/quest"));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        LOG.info("Loaded quest datapack");

        jsonElementMap.forEach((rl, jsonElement) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (obj.get("id") != null && obj.get("goals") != null) {
                String questId = "." + obj.get("id").toString().substring(1, obj.get("id").toString().length() - 1);
                Path path = Path.of(rl.getNamespace() + questId + ".json");

                datapackQuestList.put(path.toString(), obj);
            }
        });
    }
}
