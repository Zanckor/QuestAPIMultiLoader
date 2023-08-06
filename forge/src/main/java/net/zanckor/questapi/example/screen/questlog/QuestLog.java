package net.zanckor.questapi.example.screen.questlog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.zanckor.questapi.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserGoal;
import net.zanckor.questapi.api.filemanager.quest.codec.user.UserQuest;
import net.zanckor.questapi.api.filemanager.quest.register.QuestTemplateRegistry;
import net.zanckor.questapi.api.registrymanager.EnumRegistry;
import net.zanckor.questapi.api.screenmanager.AbstractQuestLog;
import net.zanckor.questapi.example.screen.button.TextButton;
import net.zanckor.questapi.mod.common.network.SendQuestPacket;
import net.zanckor.questapi.mod.common.network.handler.ClientHandler;
import net.zanckor.questapi.mod.common.network.message.screen.RequestActiveQuests;
import net.zanckor.questapi.mod.common.util.MCUtilClient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.mod.common.network.handler.ClientHandler.activeQuestList;
import static net.zanckor.questapi.mod.common.network.handler.ClientHandler.trackedQuestList;

public class QuestLog extends AbstractQuestLog {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(MOD_ID, "textures/gui/questlog_api.png");
    int selectedPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;
    EditBox textButton;
    String questSearch;
    int questInfoScroll;
    float sin;

    UserQuest selectedQuest;


    public QuestLog(Component component) {
        super(component);
    }

    @Override
    public Screen modifyScreen() {
        init();
        return this;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        selectedQuest = null;

        questInfoScroll = 0;
        imageWidth = width / 2;
        imageHeight = width / 3;
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) height / 4;
        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.25));
        int yButtonPosition = (int) (yScreenPos + ((((float) width) / 500) * 40));

        float textLengthScale = ((float) width) / 575;
        float buttonScale = ((float) width) / 700;
        int maxLength = 26 * 5;

        HashMap<Integer, Integer> displayedButton = new HashMap<>();


        //For each quest, checks if it can be added as a button to display data
        for (int i = 0; i < activeQuestList.size(); i++) {
            int buttonIndex = i + (4 * selectedPage);

            //Only add widget if there's 4 or fewer buttons added
            if (displayedButton.size() < 4 && activeQuestList.size() > buttonIndex) {
                String title = I18n.get(activeQuestList.get(buttonIndex).getTitle());

                int textLines = (title.length() * 5) / maxLength;
                float buttonWidth = textLines < 1 ? activeQuestList.get(buttonIndex).getTitle().length() * 5 * textLengthScale : maxLength * textLengthScale;

                Button questSelect = new TextButton(
                        xButtonPosition, yButtonPosition, (int) buttonWidth, 20, buttonScale,
                        Component.literal(title), 26, button -> {
                    selectedQuest = activeQuestList.get(buttonIndex);
                    SendQuestPacket.TO_SERVER(new RequestActiveQuests());
                });

                displayedButton.put(displayedButton.size() + 1, i);

                questSelect.setY((int) (questSelect.getY() + buttonIndex * (30 * buttonScale)));
                addRenderableWidget(questSelect);
            }
        }

        Button prevPage = MCUtilClient.createButton((int) (xScreenPos - (imageWidth / 5.5)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage > 0) {
                selectedPage--;

                init();
            }
        });
        Button nextPage = MCUtilClient.createButton((int) (xScreenPos - (imageWidth / 9)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage + 1 < Math.ceil(activeQuestList.size()) / 4) {
                selectedPage++;

                init();
            }
        });
        Button addTrackedQuest = MCUtilClient.createButton((int) (xScreenPos + (imageWidth / 25)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal("Track Quest"), button -> {
            AtomicBoolean containsSelectedQuest = new AtomicBoolean(false);

            trackedQuestList.forEach(trackedQuest -> {
                if (trackedQuest.getId().equals(selectedQuest.getId())) {
                    containsSelectedQuest.set(true);
                }
            });

            ClientHandler.modifyTrackedQuests(!containsSelectedQuest.get(), selectedQuest);
        });
        textButton = new EditBox(font, (int) (xScreenPos - (imageWidth / 2.4)), (int) (yScreenPos + imageHeight * 0.75), width / 6, 10, Component.literal(""));


        addRenderableWidget(textButton);
        addWidget(addTrackedQuest);
        addWidget(prevPage);
        addWidget(nextPage);

        textButton.setValue("Quest name - Press Enter");
        SendQuestPacket.TO_SERVER(new RequestActiveQuests());
    }

    @Override
    public boolean keyReleased(int x, int y, int p_94717_) {
        //Update the search result of quests by name each time player press enter
        if (textButton != null && x == 257 && y == 28) {
            questSearch = textButton.getValue();
            init();
        }

        return super.keyReleased(x, y, p_94717_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int p_94697_) {
        //Once you click your mouse, the search bar will restart
        textButton.setValue("");

        return super.mouseClicked(x, y, p_94697_);
    }

    /**
     * Renders quest log textures and text as goals, quest titles...
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int x, int y, float partialTicks) {
        float scale = ((float) width) / 500;

        //Changes the scale of gui so the enableScissor won't bug on different sizes. Then come back to previousScale
        PoseStack poseStack = graphics.pose();

        //Render background texture
        Minecraft.getInstance().getProfiler().push("background");
        graphics.blit(QUEST_LOG, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        //Render quest data as current progress, goals...
        renderQuestData(graphics, poseStack);

        //Render the text for "Quest Title" and "Quest Info"
        MCUtilClient.renderText(graphics, poseStack, (float) (xScreenPos - imageWidth / 2.9), (float) (yScreenPos + (20 * scale)), 0, scale, 40, "Quest Title", font);
        MCUtilClient.renderText(graphics, poseStack, (float) (xScreenPos + imageWidth * 0.135), (float) (yScreenPos + (20 * scale)), 0, scale, 40, "Quest Info", font);

        Minecraft.getInstance().getProfiler().pop();

        super.render(graphics, x, y, partialTicks);
    }


    /**
     * Renders quest data as quest goals and the progress
     */
    public void renderQuestData(GuiGraphics graphics, PoseStack poseStack) {
        //If no quest is selected, or it is completed, do not keep rendering the data
        if (selectedQuest == null || selectedQuest.isCompleted()) return;

        HashMap<String, List<UserGoal>> userQuestHashMap = createGoalTypesMap();
        int xPosition = (int) (width / 1.925);
        float scale = ((float) width) / 700;

        poseStack.pushPose();
        poseStack.translate(xPosition, yScreenPos + ((((float) width) / 500) * 40), 0);
        poseStack.scale(scale, scale, 0);

        //Displays quest goals
        renderSelectedQuestTitle(graphics, poseStack, minecraft);
        renderSelectedQuestData(graphics, poseStack, minecraft, userQuestHashMap);

        //If any goal has a time limit, add it as data, and update it each second to check how much time is left
        if (selectedQuest.hasTimeLimit()) {
            MCUtilClient.renderLine(graphics, poseStack, 0, 0, 30, I18n.get("tracker.questapi.time_limit") + selectedQuest.getTimeLimitInSeconds(), font);
        }

        poseStack.popPose();
    }

    HashMap<String, List<UserGoal>> createGoalTypesMap() {
        HashMap<String, List<UserGoal>> goalTypesMap = new HashMap<>();

      /*Create a key for each QuestType (Kill, Collect, MoveTo...) based on player's quests.
      *Then for each key add the goals of that type, for example, for Kill key, adds all the goals that consist on
      kill an entity*/
        for (UserGoal questGoal : selectedQuest.getQuestGoals()) {
            String type = questGoal.getType();
            List<UserGoal> questGoalList = goalTypesMap.get(type);

            //Create a new list for this key if it doesn't already exist
            if (questGoalList == null) {
                questGoalList = new ArrayList<>();
            }

            //Adds the goal to the current list and then update the list to the hashMap
            questGoalList.add(questGoal);
            goalTypesMap.put(type, questGoalList);
        }

        return goalTypesMap;
    }


    /**
     * Renders the title of selected quest
     */
    public void renderSelectedQuestTitle(GuiGraphics graphics, PoseStack poseStack, Minecraft minecraft) {
        String title = I18n.get(selectedQuest.getTitle());
        MCUtilClient.renderLines(graphics, poseStack, 25, 10, 30, I18n.get("tracker.questapi.quest") + title, minecraft.font);
    }

    /**
     * Renders the data of all goals of selected quest
     */
    public void renderSelectedQuestData(GuiGraphics graphics, PoseStack poseStack, Minecraft minecraft, HashMap<String, List<UserGoal>> goalTypesMap) {
        int scissorBottom = (int) (height - (yScreenPos + imageHeight) + 15) * 2;
        int scissorTop = (int) (imageHeight * 1.25) - 15;

        //Enable scissoring for cut rendering if text is out of the box, so it won't render text out of the book
        RenderSystem.enableScissor(width, scissorBottom, (width / 2) + (imageWidth / 2), scissorTop);

        Player player = minecraft.player;
        poseStack.translate(0, questInfoScroll + 20, 0);
        sin += 0.5;

        //Render the data of each goal, in order of goal type. So all Kill goals will render in a list, in order.
        for (Map.Entry<String, List<UserGoal>> entry : goalTypesMap.entrySet()) {
            List<UserGoal> questGoalList = entry.getValue();

            //Display text of the goal quest type.
            MCUtilClient.renderLine(graphics, poseStack, 30, 0, 0, 10, Component.literal(I18n.get("tracker.questapi.quest_type") +
                    I18n.get("quest_type." + questGoalList.get(0).getTranslatableType().toLowerCase())).withStyle(ChatFormatting.BLACK), font);

            //Display the data of each goal
            for (UserGoal questGoal : questGoalList) {
                Enum goalEnum = EnumRegistry.getEnum("TARGET_TYPE_" + questGoal.getType(), EnumRegistry.getTargetType());

                AbstractTargetType translatableTargetType = QuestTemplateRegistry.getTranslatableTargetType(goalEnum);
                MutableComponent goalComponentTarget = translatableTargetType.handler(questGoal.getTarget(), questGoal, player, ChatFormatting.GRAY, ChatFormatting.BLACK);

                translatableTargetType.renderTarget(poseStack, (goalComponentTarget.getString().length() * 6 - 4), 3, 0.7, Math.sin(sin), questGoal, questGoal.getTarget());
                MCUtilClient.renderLine(graphics, poseStack, 30, 0, 0, 10, goalComponentTarget.withStyle(ChatFormatting.ITALIC), font);
            }

            //Translate 10px for display the next goal
            poseStack.translate(0, 10, 0);
        }

        RenderSystem.disableScissor();
    }


    /**
     * On scroll mouse wheel, the displayed data of the quest will move up and down, so player will be able to see all text
     * even if its out of the book
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        boolean mouseXInBox = (mouseX > xScreenPos) && (mouseX < (xScreenPos + (double) imageWidth / 2));
        boolean mouseYInBox = (mouseY > yScreenPos) && (mouseY < (yScreenPos + imageHeight));

        if (mouseXInBox && mouseYInBox) {
            questInfoScroll += scroll * 4;
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
