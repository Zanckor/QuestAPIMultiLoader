package net.zanckor.questapi.mod.common.datapack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.zanckor.questapi.CommonMain;
import net.zanckor.questapi.commonutil.GsonManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static net.zanckor.questapi.CommonMain.Constants.LOG;

public class DialogJSONListener extends SimpleJsonResourceReloadListener {
    public static HashMap<String, JsonObject> datapackDialogList = new HashMap<>();

    public DialogJSONListener(Gson gson, String name) {
        super(gson, name);
    }

    public static void register(AddReloadListenerEvent e) {
        e.addListener(new DialogJSONListener(GsonManager.gson, "assets/questapi/dialog"));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        LOG.info("Loaded dialog datapack");

        jsonElementMap.forEach((rl, jsonElement) -> {
            JsonObject obj = jsonElement.getAsJsonObject();

            //Load dialog
            if(obj.get("dialog") != null){
                String dialogID = "." + rl.getPath();
                Path path = Path.of(rl.getNamespace() + dialogID + ".json");

                datapackDialogList.put(path.toString(), obj);
            }
        });
    }
}
