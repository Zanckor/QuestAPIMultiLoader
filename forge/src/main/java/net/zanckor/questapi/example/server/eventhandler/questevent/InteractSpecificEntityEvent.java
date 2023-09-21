package net.zanckor.questapi.example.server.eventhandler.questevent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zanckor.questapi.util.Timer;
import net.zanckor.questapi.mod.common.network.handler.ServerHandler;

import java.io.IOException;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumquest.EnumGoalType.INTERACT_SPECIFIC_ENTITY;


@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractSpecificEntityEvent {

    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) throws IOException {
        if (e.getHand() == InteractionHand.OFF_HAND || e.getSide().isClient() || !(e.getTarget() instanceof LivingEntity))
            return;


        if (Timer.canUseWithCooldown(e.getEntity().getUUID(), "INTERACT_SPECIFIC_EVENT_COOLDOWN", 1)) {
            ServerHandler.questHandler(INTERACT_SPECIFIC_ENTITY, (ServerPlayer) e.getEntity(), (LivingEntity) e.getTarget());
            Timer.updateCooldown(e.getEntity().getUUID(), "INTERACT_SPECIFIC_EVENT_COOLDOWN", 1);
        }
    }
}