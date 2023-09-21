package net.zanckor.questapi;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.zanckor.questapi.api.screen.AbstractQuestTracked;
import net.zanckor.questapi.api.registry.ScreenRegistry;
import net.zanckor.questapi.example.ModExample;
import net.zanckor.questapi.mod.common.config.client.RendererConfig;
import net.zanckor.questapi.mod.common.config.client.ScreenConfig;
import net.zanckor.questapi.mod.common.network.NetworkHandler;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@Mod(MOD_ID)
public class ForgeQuestAPI {

    public ForgeQuestAPI() {
        CommonMain.init();

        LOG.info("Loading QuestAPI to Forge");
        new ModExample();
        MinecraftForge.EVENT_BUS.register(this);
        NetworkHandler.register();

        LOG.info("Registering config files");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ScreenConfig.SPEC, "questapi-screen.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RendererConfig.SPEC, "questapi-renderer.toml");
    }
}