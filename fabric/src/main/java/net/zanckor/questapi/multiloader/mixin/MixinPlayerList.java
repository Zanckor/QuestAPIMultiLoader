package net.zanckor.questapi.multiloader.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        PlayerEvent.PlayerConnectionServerEvent.PLAYER_JOIN_SERVER.invoker().playerJoinServer(serverPlayer);
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void remove(ServerPlayer serverPlayer, CallbackInfo ci) {
        PlayerEvent.PlayerConnectionServerEvent.PLAYER_LEAVE_SERVER.invoker().playerLeaveServer(serverPlayer);
    }
}
