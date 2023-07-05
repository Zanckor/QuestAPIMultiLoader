package net.zanckor.questapi.mod.common.network.message.dialogoption;

import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CloseDialog {

    public CloseDialog() {
    }

    public CloseDialog(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(CloseDialog msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());
        });

        ctx.get().setPacketHandled(true);
    }
}
