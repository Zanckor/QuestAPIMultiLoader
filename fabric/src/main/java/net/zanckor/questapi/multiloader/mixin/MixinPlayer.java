package net.zanckor.questapi.multiloader.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import net.zanckor.questapi.eventmanager.event.PlayerEvent.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {
    private int previousXP;

    @Shadow
    public int experienceLevel;

    @Shadow
    public abstract Inventory getInventory();

    @Shadow
    public abstract boolean isSpectator();

    @Inject(method = "tick", at = @At("HEAD"))
    public void playerChangeInventoryEvent(CallbackInfo ci) {
        if (previousXP != experienceLevel && !getInventory().player.level().isClientSide) {
            previousXP = experienceLevel;

            PlayerEvent.PlayerXPEvent.PLAYER_CHANGE_XP.invoker().playerChangeXP(getInventory().player);
        }
    }


    @Inject(method = "interactOn", at = @At("TAIL"))
    public void interactEvent(Entity entity, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!this.isSpectator() && !getInventory().player.level().isClientSide) {
            PlayerInteractEvent.PLAYER_INTERACT_ENTITY.invoker().playerInteractEntity((ServerPlayer) getInventory().player, entity, interactionHand);
        }
    }
}
