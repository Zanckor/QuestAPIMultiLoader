package net.zanckor.questapi.eventmanager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import net.zanckor.questapi.eventmanager.annotation.Side;
import net.zanckor.questapi.eventmanager.annotation.SubscribeEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerConnectionServerEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerConnectionServerEvent.PlayerJoinServer;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent.PlayerInteractEntity;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent.PlayerInteractItem;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInventoryEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInventoryEvent.ChangeInventory;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerXPEvent;
import net.zanckor.questapi.eventmanager.event.RenderLivingEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    static List<Class<?>> eventListeners = new ArrayList<>();

    public static void registerClass() {
        List<Class<?>> annotatedClasses = RegisterEventListener.getAnnotatedClasses();

        //Adds to eventHandlers each class with annotation @EventSubscriber
        annotatedClasses.forEach(clazz -> {
            if (!eventListeners.contains(clazz)) {
                eventListeners.add(clazz);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static void call(final Side side, final Class<?> event, final Object... eventArgs) {
        for (Class<?> eventListener : eventListeners) {
            EventSubscriber classAnnotation = eventListener.getAnnotation(EventSubscriber.class);
            Side annotationSide = classAnnotation.side();

            //If class has @EventSubscriber annotation and is in the correct side, keep checking each method
            if (classAnnotation != null) {
                if (annotationSide.equals(side) || annotationSide.equals(Side.COMMON) || side.equals(Side.COMMON)) {
                    Method[] methods = eventListener.getDeclaredMethods();

                    //Checks if method has an @SubscribeEvent annotation to execute an event
                    for (Method method : methods) {
                        SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);

                        if (annotation != null) {
                            Class<?> methodEvent = annotation.event();

                            //Invoke if this method has @SubscribeEvent annotation, and it's for the same event
                            try {
                                if (annotation != null && methodEvent.equals(event)) {
                                    //Turn Object... into Object[], because Object... is read as one single parameter instead of the real number

                                    //Call the method
                                    method.invoke(null, eventArgs);
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void serverPlayerEvent() {
        PlayerConnectionServerEvent.PLAYER_JOIN_SERVER.register(player ->
                call(Side.SERVER, PlayerJoinServer.class, player.getServer(), player));

        PlayerInventoryEvent.PLAYER_CHANGE_INVENTORY.register(player ->
                call(Side.SERVER, ChangeInventory.class, player));

        PlayerInteractEvent.PLAYER_INTERACT_ITEM.register((player, itemStack) ->
                call(Side.SERVER, PlayerInteractItem.class, player, itemStack));

        PlayerInteractEvent.PLAYER_INTERACT_ENTITY.register((player, entity, interactionHand) ->
                call(Side.SERVER, PlayerInteractEntity.class, player, entity, interactionHand));

        PlayerXPEvent.PLAYER_CHANGE_XP.register(player ->
                call(Side.SERVER, PlayerXPEvent.class, player));
    }

    public static void clientPlayerEvent() {
        ClientLoginConnectionEvents.INIT.register((handler, client) ->
                call(Side.CLIENT, ClientLoginConnectionEvents.Init.class, handler, client));
    }

    public static void serverTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            //Call ServerTickEvent
            call(Side.SERVER, ServerTickEvents.EndTick.class, server);

            //Callback for player tick events
            server.getPlayerList().getPlayers().forEach(player -> {
                PlayerEvent.PlayerTickEvent.PLAYER_TICK.invoker().playerTickEvent(player);
                call(Side.SERVER, PlayerEvent.PlayerTickEvent.class, player);
            });
        });
    }

    public static void clientTickEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            call(Side.COMMON, ClientTickEvents.EndTick.class, client);
        });
    }

    public static void renderHUDEvent() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
                call(Side.CLIENT, HudRenderCallback.class, drawContext, tickDelta));
    }

    public static void serverCombatEvent() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) ->
                call(Side.SERVER, ServerEntityCombatEvents.AfterKilledOtherEntity.class, world, entity, killedEntity));
    }

    public static void serverLifeCycleEvent() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            call(Side.SERVER, ServerLifecycleEvents.ServerStarting.class, server);
        });
    }

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            call(Side.SERVER, CommandRegistrationCallback.class, dispatcher, registryAccess, environment);
        });
    }

    public static void renderEntity() {
        RenderLivingEvent.AFTER_RENDER_LIVING_ENTITY.register((entity, f, g, poseStack, multiBufferSource, i) ->
                call(Side.CLIENT, RenderLivingEvent.AfterRenderLivingEntity.class, entity, f, g, poseStack, multiBufferSource, i));
    }
}
