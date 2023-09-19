package net.zanckor.questapi.example.core.registry;


import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.zanckor.questapi.example.common.entity.server.NPCEntity;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@SuppressWarnings("all")
public class NpcRegistry {
    private static EntityType<?> register(String name, EntityType<NPCEntity> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(MOD_ID, name), entityType);
    }

    public static final EntityType<? extends Villager> NPC_ENTITY = (EntityType<? extends Villager>) register("quest_npc",
            FabricEntityTypeBuilder.<NPCEntity>create(MobCategory.MISC, NPCEntity::new)
                    .build());


    public static void registerEntity() {
        LOG.info("Registering Entity Types for " + MOD_ID);
    }

    public static void registerAttribute() {
        FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) NPC_ENTITY, NPCEntity.setAttributes());
    }
}
