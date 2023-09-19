package net.zanckor.questapi.mod.core.data;

import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CompoundTag;

public interface IEntityData {
    CompoundTag getPersistentData();
}
