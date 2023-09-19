package net.zanckor.questapi.example.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zanckor.questapi.example.common.entity.server.NPCEntity;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class NpcRegistry {
    public static final DeferredRegister<EntityType<?>>
            ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);


    public static final RegistryObject<EntityType<NPCEntity>> NPC_ENTITY = ENTITIES.register("quest_npc",
            () -> EntityType.Builder.of(NPCEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(MOD_ID, "quest_npc").toString()));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
