package net.zanckor.questapi.example.client.screen.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.zanckor.questapi.api.screenmanager.AbstractDialog;
import net.zanckor.questapi.api.screenmanager.NpcType;
import net.zanckor.questapi.example.client.screen.button.TextButton;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.AddQuest;
import net.zanckor.questapi.mod.common.network.packet.dialogoption.DialogRequestPacket;
import net.zanckor.questapi.mod.common.network.packet.screen.OpenVanillaEntityScreen;
import net.zanckor.questapi.mod.common.util.MCUtilClient;
import net.zanckor.questapi.mod.core.filemanager.dialogquestregistry.enumdialog.EnumDialogOption;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;

@SuppressWarnings("unused, ConstantConditions")
public class DialogScreen extends AbstractDialog {
    int dialogID;
    String text;
    int textDisplayDelay;
    int textDisplaySize;
    int optionSize;
    HashMap<Integer, List<Integer>> optionIntegers;
    HashMap<Integer, List<String>> optionStrings;

    double xScreenPos, yScreenPos, scale;
    int imageWidth, imageHeight;
    int xButtonPosition, yButtonPosition;
    Entity entity;
    UUID npcUUID;
    String resourceLocation;
    Item item;
    NpcType npcType;


    private final static ResourceLocation DIALOG = new ResourceLocation(MOD_ID, "textures/gui/dialog_background.png");
    private final static ResourceLocation BUTTON = new ResourceLocation(MOD_ID, "textures/gui/dialog_button.png");


    public DialogScreen(Component component) {
        super(component);
    }


    @Override
    public Screen modifyScreen(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npcUUID, String resourceLocation, Item item, NpcType npcType) {
        this.dialogID = dialogID;
        this.text = I18n.get(text);
        this.optionSize = optionSize;
        this.optionIntegers = optionIntegers;
        this.optionStrings = optionStrings;
        this.npcType = npcType;

        switch (npcType) {
            case RESOURCE_LOCATION -> {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(resourceLocation));
                entity = entityType.create(Minecraft.getInstance().level);
                this.resourceLocation = resourceLocation;
            }

            case UUID -> entity = MCUtilClient.getEntityByUUID(npcUUID);
            case ITEM -> this.item = item;
        }

        this.npcUUID = npcUUID;

        return this;
    }

    @Override
    protected void init() {
        super.init();

        textDisplaySize = 0;
        imageWidth = width / 2;
        imageHeight = (int) (width / 2.7);
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) width / 11;
        scale = ((float) width) / 700;

        xButtonPosition = (int) (width / 3.55);
        yButtonPosition = (int) (yScreenPos * 3.6);

        for (int i = 0; i < optionSize; i++) {
            int stringLength = (int) ((optionStrings.get(i).get(0).length() + 1) * 5.5);
            int index = i;


            if (xButtonPosition + stringLength > (width / 1.4)) {
                xButtonPosition = (int) (width / 3.55);
                yButtonPosition += 22 * scale;
            }

            addRenderableWidget(new TextButton(xButtonPosition, yButtonPosition, (int) (stringLength * scale), 20, ((float) width) / 675,
                    Component.literal(I18n.get(optionStrings.get(i).get(0))), 26, button -> button(index)));

            xButtonPosition += optionStrings.get(i).get(0).length() * 5.7 * scale;
        }

        addRenderableWidget(new TextButton((int) (imageWidth * 1.4), (int) (imageHeight * 1.1), 20, 20, ((float) width) / 300,
                Component.literal("↩"), 26, button -> {
            if (npcUUID != null) SendQuestPacket.TO_SERVER(new OpenVanillaEntityScreen(npcUUID));
        }));
    }

    @Override
    public void tick() {
        super.tick();

        if (textDisplaySize < text.length()) {
            if (textDisplayDelay == 0) {
                MCUtilClient.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, 0.975f, 1.025f);
                textDisplaySize++;

                if (textDisplaySize < text.length()) {
                    switch (Character.toString(text.charAt(textDisplaySize))) {
                        case ".", "?", "!" -> textDisplayDelay = 9;
                        case "," -> textDisplayDelay = 5;
                    }
                }
            } else {
                textDisplayDelay--;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int xPosition = (int) (width / 2.41);
        int yPosition = (int) (yScreenPos + yScreenPos / 1.45);
        PoseStack poseStack = graphics.pose();

        graphics.blit(DIALOG, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        MCUtilClient.renderText(graphics, poseStack, xPosition, yPosition, 26, (float) width / 675, 42, text.substring(0, textDisplaySize), font);

        switch (npcType) {
            case UUID, RESOURCE_LOCATION -> {
                if (entity != null) MCUtilClient.renderEntity(
                        xScreenPos / 1.4575, yScreenPos * 3.41, (double) width / 12,
                        (xScreenPos / 1.4575 - mouseX) / 4, (yScreenPos * 2.5 - mouseY) / 4,
                        (LivingEntity) entity);
            }

            case ITEM -> MCUtilClient.renderItem(item.getDefaultInstance(),
                    (int) (xScreenPos / 1.4575), (int) (yScreenPos * 2.5), (double) width / 150,
                    0, poseStack);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void button(int optionID) {
        EnumDialogOption optionType = EnumDialogOption.valueOf(optionStrings.get(optionID).get(1));

        switch (optionType) {
            case OPEN_DIALOG, CLOSE_DIALOG ->
                    SendQuestPacket.TO_SERVER(new DialogRequestPacket(optionType, optionID, entity, item, npcType));
            case ADD_QUEST -> SendQuestPacket.TO_SERVER(new AddQuest(optionType, optionID));
        }
    }

    @Override
    public boolean mouseClicked(double xPosition, double yPosition, int button) {
        textDisplaySize = text.length();

        return super.mouseClicked(xPosition, yPosition, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
