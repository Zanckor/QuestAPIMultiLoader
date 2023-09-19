package net.zanckor.questapi.mod.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.AddQuest;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.CloseDialog;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DialogRequestPacket;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DisplayDialog;
import net.zanckor.questapi.mod.common.network.packet.npcmarker.ValidNPCMarker;
import net.zanckor.questapi.mod.common.network.packet.quest.ActiveQuestList;
import net.zanckor.questapi.mod.common.network.packet.quest.QuestDataPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.TimerPacket;
import net.zanckor.questapi.mod.common.network.packet.quest.ToastPacket;
import net.zanckor.questapi.mod.common.network.packet.screen.*;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

public class NetworkHandler {
    public static final ResourceLocation ADD_QUEST = new ResourceLocation(MOD_ID, "add_quest");
    public static final ResourceLocation CLOSE_DIALOG = new ResourceLocation(MOD_ID, "close_dialog");
    public static final ResourceLocation DIALOG_REQUEST_PACKET = new ResourceLocation(MOD_ID, "dialog_request_packet");
    public static final ResourceLocation DISPLAY_DIALOG = new ResourceLocation(MOD_ID, "display_dialog");
    public static final ResourceLocation VALID_NPC_MARKER = new ResourceLocation(MOD_ID, "valid_npc_marker");
    public static final ResourceLocation ACTIVE_QUEST_LIST = new ResourceLocation(MOD_ID, "active_quest_list");
    public static final ResourceLocation QUEST_DATA_PACKET = new ResourceLocation(MOD_ID, "quest_data_packet");
    public static final ResourceLocation TIMER_PACKET = new ResourceLocation(MOD_ID, "timer_packet");
    public static final ResourceLocation TOAST_PACKET = new ResourceLocation(MOD_ID, "toast_packer");
    public static final ResourceLocation MODIFY_TRACKED_QUESTS = new ResourceLocation(MOD_ID, "modify_tracked_quests");
    public static final ResourceLocation OPEN_VANILLA_ENTITY_SCREEN = new ResourceLocation(MOD_ID, "open_vanilla_entity_screen");
    public static final ResourceLocation REMOVE_QUEST = new ResourceLocation(MOD_ID, "remove_quest");
    public static final ResourceLocation REQUEST_ACTIVE_QUEST = new ResourceLocation(MOD_ID, "request_active_quest");
    public static final ResourceLocation UPDATE_QUEST_TRACKED = new ResourceLocation(MOD_ID, "update_quest_tracked");

    public static void registerServerReceiverPacket() {
        ServerPlayNetworking.registerGlobalReceiver(ADD_QUEST, AddQuest::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ACTIVE_QUEST, RequestActiveQuests::receive);
        ServerPlayNetworking.registerGlobalReceiver(OPEN_VANILLA_ENTITY_SCREEN, OpenVanillaEntityScreen::receive);
        ServerPlayNetworking.registerGlobalReceiver(TIMER_PACKET, TimerPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(QUEST_DATA_PACKET, QuestDataPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(DIALOG_REQUEST_PACKET, DialogRequestPacket::receive);
    }

    public static void registerClientReceiverPacket() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_QUEST_TRACKED, UpdateQuestTracked::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_QUEST, RemoveQuest::receive);
        ClientPlayNetworking.registerGlobalReceiver(MODIFY_TRACKED_QUESTS, ModifyTrackedQuests::receive);
        ClientPlayNetworking.registerGlobalReceiver(TOAST_PACKET, ToastPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ACTIVE_QUEST_LIST, ActiveQuestList::receive);
        ClientPlayNetworking.registerGlobalReceiver(VALID_NPC_MARKER, ValidNPCMarker::receive);
        ClientPlayNetworking.registerGlobalReceiver(DISPLAY_DIALOG, DisplayDialog::receive);
        ClientPlayNetworking.registerGlobalReceiver(CLOSE_DIALOG, CloseDialog::receive);
    }
}
