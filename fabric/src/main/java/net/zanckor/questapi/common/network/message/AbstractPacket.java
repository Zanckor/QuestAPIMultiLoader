package net.zanckor.questapi.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractPacket {
    public abstract FriendlyByteBuf encode();
    public abstract ResourceLocation getID();
}
