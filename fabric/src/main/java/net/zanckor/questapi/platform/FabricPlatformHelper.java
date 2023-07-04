package net.zanckor.questapi.platform;

import net.zanckor.questapi.multiloader.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.zanckor.questapi.multiloader.platform.services.PlatformEnum;

import static net.zanckor.questapi.multiloader.platform.services.PlatformEnum.FABRIC;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public PlatformEnum getPlatform() {
        return FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
