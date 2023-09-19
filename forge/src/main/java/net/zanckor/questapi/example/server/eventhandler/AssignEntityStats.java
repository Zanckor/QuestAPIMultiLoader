package net.zanckor.questapi.example.server.eventhandler;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.example.core.registry.NpcRegistry;
import net.zanckor.questapi.example.common.entity.server.NPCEntity;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AssignEntityStats {
    @SubscribeEvent
    public static void assignEntityAtts(EntityAttributeCreationEvent e) {
        e.put(NpcRegistry.NPC_ENTITY.get(), NPCEntity.setAttributes());
    }
}
