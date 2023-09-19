package net.zanckor.questapi.multiloader.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zanckor.questapi.eventmanager.event.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int pickupDelay;

    @Shadow
    private UUID target;


    @Inject(method = "playerTouch", at = @At("TAIL"))
    private void onTouch(Player player, CallbackInfo ci) {
        if (this.pickupDelay == 0 && (this.target == null || this.target.equals(player.getUUID()))) {
            PlayerEvent.PlayerInventoryEvent.PLAYER_CHANGE_INVENTORY.invoker().playerInventory((ServerPlayer) player);
        }
    }
}
