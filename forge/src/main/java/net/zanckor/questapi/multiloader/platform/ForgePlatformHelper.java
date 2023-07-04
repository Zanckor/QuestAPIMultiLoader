package net.zanckor.questapi.multiloader.platform;

import net.zanckor.questapi.multiloader.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.zanckor.questapi.multiloader.platform.services.PlatformEnum;

import static net.zanckor.questapi.multiloader.platform.services.PlatformEnum.FORGE;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public PlatformEnum getPlatform() {
        return FORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}