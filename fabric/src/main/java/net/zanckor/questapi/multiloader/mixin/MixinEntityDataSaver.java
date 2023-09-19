package net.zanckor.questapi.multiloader.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.zanckor.questapi.mod.core.data.IEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntityDataSaver implements IEntityData {
    private CompoundTag persistentData;

    @Override
    public CompoundTag getPersistentData() {
        if (persistentData == null) {
            persistentData = new CompoundTag();
        }

        return persistentData;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    public void saveWithoutId(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        if (persistentData != null) {
            compoundTag.put("questapi.entity_data", persistentData);
        }
    }


    @Inject(method = "load", at = @At("HEAD"))
    public void load(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("questapi.entity_data")) {
            persistentData = compoundTag.getCompound("questapi.entity_data");
        }
    }
}
