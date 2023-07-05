package net.zanckor.questapi.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class NetworkHandler {
    public static final ResourceLocation ADD_QUEST = new ResourceLocation(MOD_ID, "add_quest");

    public static void registerServerReceiverPacket(){
        //ServerPlayNetworking.registerGlobalReceiver(ADD_QUEST, AddQuest::receive);
    }

    public static void registerClientReceiverPacket(){

    }
}
