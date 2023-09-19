package net.zanckor.questapi.eventmanager.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerEvent {
    public static class PlayerInventoryEvent {
        public static final Event<ChangeInventory> PLAYER_CHANGE_INVENTORY = EventFactory.createArrayBacked(ChangeInventory.class,
                callBacks -> (player -> {
                    for (ChangeInventory callback : callBacks) {
                        callback.playerInventory(player);
                    }
                }));

        @FunctionalInterface
        public interface ChangeInventory {
            /**
             * Called on change inventory
             **/

            void playerInventory(ServerPlayer player);
        }
    }

    public static final class PlayerTickEvent {

        public static final Event<PlayerTick> PLAYER_TICK = EventFactory.createArrayBacked(PlayerTick.class,
                callbacks -> ((player) -> {
                    for (PlayerTick callback : callbacks) {
                        callback.playerTickEvent(player);
                    }
                }));


        @FunctionalInterface
        public interface PlayerTick {
            /**
             * Called after an entity as being rendered
             */

            void playerTickEvent(Player player);
        }
    }

    public static class PlayerInteractEvent {
        public static final Event<PlayerInteractEntity> PLAYER_INTERACT_ENTITY = EventFactory.createArrayBacked(PlayerInteractEntity.class,
                callbacks -> ((player, entity, interactionHand) -> {
                    for (PlayerInteractEntity callback : callbacks) {
                        callback.playerInteractEntity(player, entity, interactionHand);
                    }
                }));
        public static final Event<PlayerInteractItem> PLAYER_INTERACT_ITEM = EventFactory.createArrayBacked(PlayerInteractItem.class,
                callbacks -> ((player, itemStack) -> {
                    for (PlayerInteractItem callback : callbacks) {
                        callback.playerInteractItem(player, itemStack);
                    }
                }));

        @FunctionalInterface
        public interface PlayerInteractEntity {
            /**
             * Called after interact on any entity
             */

            void playerInteractEntity(ServerPlayer player, Entity entity, InteractionHand interactionHand);
        }

        @FunctionalInterface
        public interface PlayerInteractItem {
            /**
             * Called after interact on any item
             */

            void playerInteractItem(ServerPlayer player, ItemStack itemStack);
        }
    }

    public static class PlayerConnectionServerEvent {
        public static final Event<PlayerJoinServer> PLAYER_JOIN_SERVER = EventFactory.createArrayBacked(PlayerJoinServer.class,
                callbacks -> (player -> {
                    for (PlayerJoinServer callback : callbacks) {
                        callback.playerJoinServer(player);
                    }
                }));

        public static final Event<PlayerLeaveServer> PLAYER_LEAVE_SERVER = EventFactory.createArrayBacked(PlayerLeaveServer.class,
                callbacks -> (player -> {
                    for (PlayerLeaveServer callback : callbacks) {
                        callback.playerLeaveServer(player);
                    }
                }));

        @FunctionalInterface
        public interface PlayerJoinServer {
            /**
             * Called when player joins the server
             */

            void playerJoinServer(Player player);
        }

        @FunctionalInterface
        public interface PlayerLeaveServer {
            /**
             * Called when player leaves the server
             */

            void playerLeaveServer(Player player);
        }
    }

    public static class PlayerXPEvent {
        public static final Event<PlayerChangeXP> PLAYER_CHANGE_XP = EventFactory.createArrayBacked(PlayerChangeXP.class,
                callbacks -> (player -> {
                    for (PlayerChangeXP callback : callbacks) {
                        callback.playerChangeXP(player);
                    }
                }));

        @FunctionalInterface
        public interface PlayerChangeXP {
            /**
             * Called whenever XP changes
             */

            void playerChangeXP(Player player);
        }
    }
}
