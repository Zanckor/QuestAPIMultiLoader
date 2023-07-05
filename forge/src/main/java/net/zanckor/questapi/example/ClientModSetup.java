package net.zanckor.questapi.example;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.zanckor.questapi.example.core.registry.NpcRegistry;
import net.zanckor.questapi.example.common.entity.client.NPCRenderer;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;


@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModSetup {

    @SubscribeEvent
    public static void registerOverlays(FMLClientSetupEvent e) {
        EntityRenderers.register(NpcRegistry.NPC_ENTITY.get(), NPCRenderer::new);
    }
}
