package net.zanckor.questapi.multiloader.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class MixinFurnaceResultSlot {

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTake(Player player, ItemStack itemStack, CallbackInfo ci){
        PlayerEvent.PlayerInventoryEvent.PLAYER_CHANGE_INVENTORY.invoker().playerInventory((ServerPlayer) player);
    }
}
