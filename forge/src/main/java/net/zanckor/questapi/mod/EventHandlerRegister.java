package net.zanckor.questapi.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import net.zanckor.questapi.mod.common.datapack.CompoundTagDialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.DialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.EntityTypeDialogJSONListener;
import net.zanckor.questapi.mod.common.datapack.QuestJSONListener;
import net.zanckor.questapi.mod.server.command.QuestCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.zanckor.questapi.CommonMain.Constants.LOG;
import static net.zanckor.questapi.CommonMain.Constants.MOD_ID;
import static net.zanckor.questapi.CommonMain.*;

@SuppressWarnings("ConstantConditions, unused")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandlerRegister {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
        LOG.info("QuestAPI Commands registered");

        CommandDispatcher<CommandSourceStack> dispatcher = e.getDispatcher();

        dispatcher.register(Commands.literal("quests")
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::addQuestSuggestions)
                                        .executes((context) -> {
                                            try {
                                                return QuestCommand.addQuest(
                                                        context,
                                                        EntityArgument.getPlayer(context, "player").getUUID(),
                                                        StringArgumentType.getString(context, "questID"));
                                            } catch (IOException ex) {
                                                LOG.error(ex.getMessage());

                                                return 0;
                                            }
                                        })))

                        .then(Commands.literal("itemDialog")
                                .then(Commands.argument("dialogID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::addDialogSuggestions)
                                        .executes(context ->
                                                QuestCommand.putDialogToItem(
                                                        context.getSource().getPlayer().getMainHandItem(),
                                                        StringArgumentType.getString(context, "dialogID")))))

                        .then(Commands.literal("itemQuest")
                                .then(Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::addQuestSuggestions)
                                        .executes(context ->
                                                QuestCommand.putQuestToItem(
                                                        context.getSource().getPlayer().getMainHandItem(),
                                                        StringArgumentType.getString(context, "questID"))))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::removeQuestSuggestions)
                                        .executes((context) -> {
                                            try {
                                                return QuestCommand.removeQuest(
                                                        context,
                                                        EntityArgument.getPlayer(context, "player").getUUID(),
                                                        StringArgumentType.getString(context, "questID"));
                                            } catch (IOException ex) {
                                                LOG.error(ex.getMessage());

                                                return 0;
                                            }
                                        }))))

                .then(Commands.literal("displayDialog")
                        .then(Commands.argument("dialogID", StringArgumentType.string())
                                .suggests(EventHandlerRegister::addDialogSuggestions)
                                .executes(context -> {
                                    try {
                                        return QuestCommand.displayDialog(
                                                context.getSource().getPlayer(),
                                                StringArgumentType.getString(context, "dialogID")
                                        );
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }))));


        ConfigCommand.register(e.getDispatcher());
    }


    private static CompletableFuture<Suggestions> removeQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Player player = ctx.getSource().getPlayer();
        List<File[]> questsFile = new ArrayList<>();

        Path userFolder = getUserFolder(player.getUUID());

        Path activeQuestFolder = getActiveQuest(userFolder);
        Path completedQuestFolder = getCompletedQuest(userFolder);
        Path uncompletedQuestFolder = getFailedQuest(userFolder);


        questsFile.add(activeQuestFolder.toFile().listFiles());
        questsFile.add(completedQuestFolder.toFile().listFiles());
        questsFile.add(uncompletedQuestFolder.toFile().listFiles());

        for (File[] questList : questsFile) {
            for (File quest : questList) {
                builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
            }
        }

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> trackedQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Player player = ctx.getSource().getPlayer();
        Path userFolder = getUserFolder(player.getUUID());

        File[] questList = getActiveQuest(userFolder).toFile().listFiles();

        for (File quest : questList) {
            builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
        }

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> addQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        File[] questsFile = serverQuests.toFile().listFiles();

        for (File quest : questsFile) {
            builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
        }
        return builder.buildFuture();
    }


    private static CompletableFuture<Suggestions> addDialogSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        File[] questsFile = serverDialogs.toFile().listFiles();

        for (File quest : questsFile) {
            builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
        }

        return builder.buildFuture();
    }

    @SubscribeEvent
    public static void jsonListener(AddReloadListenerEvent e) {
        QuestJSONListener.register(e);
        DialogJSONListener.register(e);
        CompoundTagDialogJSONListener.register(e);
        EntityTypeDialogJSONListener.register(e);
    }
}
