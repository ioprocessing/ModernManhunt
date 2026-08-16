package org.ioprocessing.modernManhunt.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.commands.subcommands.*;
import org.ioprocessing.modernManhunt.config.*;
import org.ioprocessing.modernManhunt.items.CustomItems;
import org.ioprocessing.modernManhunt.menus.PlayerTeamsGui;
import org.ioprocessing.modernManhunt.menus.boon.BoonPlayerGui;
import org.ioprocessing.modernManhunt.menus.handicap.HandicapPlayerGui;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModernManhuntCommand {
    private final ConfigManager<BarterPoolConfig> bartering;
    private final ConfigManager<CoreConfig> coreConfig;
    private final ConfigManager<HandicapConfig> handicap;
    private final ConfigManager<BoonConfig> boon;
    private final GiveCommand give;
    private final RunnerCommand runner;
    private final HunterCommand hunter;
    private final SpectatorCommand spectator;
    private final StartStopCommand startStop;

    public ModernManhuntCommand(Core core) {
        this.bartering = core.getBartering();
        this.coreConfig = core.getCoreConfig();
        this.handicap = core.getHandicap();
        this.boon = core.getBoon();
        this.give = new GiveCommand();
        this.runner = new RunnerCommand();
        this.hunter = new HunterCommand();
        this.spectator = new SpectatorCommand();
        this.startStop = new StartStopCommand(coreConfig);
    }

    /**
     * Register the /modernmanhunt command
     */
    public void register(ReloadableRegistrarEvent<@NotNull Commands> event) {
        for(String alias : List.of("modernmanhunt", "mm")) {
            LiteralArgumentBuilder<CommandSourceStack> command =
                    Commands.literal(alias)

                            // Runner subcommands
                            .then(Commands.literal("runner")
                                    .then(Commands.literal("add")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(runner::handleAdd))) // /mm runner add
                                    .then(Commands.literal("remove")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(runner::handleRemove))) // /mm runner remove
                                    .then(Commands.literal("list")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.list"))
                                            .executes(runner::handleList)) // /mm runner list
                            )

                            // Hunter subcommands
                            .then(Commands.literal("hunter")
                                    .then(Commands.literal("add")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(hunter::handleSet))) // /mm hunter add
                                    .then(Commands.literal("remove")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(hunter::handleRemove))) // /mm hunter remove
                                    .then(Commands.literal("list")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.list"))
                                            .executes(hunter::handleList)) // /mm hunter list
                            )

                            // Spectator subcommands (/mm spectator)
                            .then(Commands.literal("spectator")
                                    .then(Commands.literal("add")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(spectator::handleAdd))) // /mm spectator add
                                    .then(Commands.literal("remove")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                            .then(Commands.argument("player", ArgumentTypes.players())
                                                    .executes(spectator::handleRemove))) // /mm spectator remove
                                    .then(Commands.literal("list")
                                            .requires(source -> source.getSender().hasPermission("modernmanhunt.command.list"))
                                            .executes(spectator::handleList)) // /mm spectator list
                            )


                            // Start/Stop commands (/mm start and /mm stop)
                            .then(Commands.literal("start")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.start"))
                                    .executes(startStop::startManhunt) // /mm start
                                    .then(Commands.argument("seed", StringArgumentType.greedyString())
                                            .executes(startStop::startManhunt))) // Optional seed argument (/mm start <seed name>)
                            .then(Commands.literal("stop")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.start"))
                                    .executes(startStop::stopManhunt)) // /mm stop

                            // Give command (/mm give <selector> <item_name> [<count>])
                            .then(Commands.literal("give")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.give"))
                                    .then(Commands.argument("player", ArgumentTypes.players())
                                            .then(Commands.argument("item", StringArgumentType.word())
                                                    .suggests((context, builder) -> {
                                                        // Suggest all item names from config
                                                        CustomItems.CUSTOM_ITEM_MAP.keySet()
                                                                .forEach(builder::suggest);
                                                        return builder.buildFuture();
                                                    })
                                                    // Execute with default count of 1 if no count specified
                                                    .executes(give::handleNoArg)
                                                    // Execute with specified count
                                                    .then(Commands.argument("count", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                                            .executes(give::handleWithCount)
                                                    )
                                            )
                                    ))

                            // Handicap management (/mm handicaps)
                            .then(Commands.literal("handicaps")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.handicaps"))
                                    .executes(this::handleHandicaps)
                            )

                            .then(Commands.literal("boons")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.boons"))
                                    .executes(this::handleBoons)
                            )

                            // Infection mode toggle (/mm teams)
                            .then(Commands.literal("teams")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.manage-teams"))
                                    .executes(this::handleTeams))

                            // Reload configs (/mm reload)
                            .then(Commands.literal("reload")
                                    .requires(source -> source.getSender().hasPermission("modernmanhunt.command.reload"))
                                    .executes(this::handleReload));
            event.registrar().register(command.build());
        }
    }

    private int handleHandicaps(CommandContext<CommandSourceStack> context) {
        try {
            CommandSender sender = context.getSource().getSender();
            if(sender instanceof Player player)
                new HandicapPlayerGui(player).open();
            else
                sender.sendMessage(SystemMessage.error("Command can only be used by players"));

            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to open handicaps GUI: {%s}", e.getMessage())));
            return 0;
        }
    }

    private int handleBoons(CommandContext<CommandSourceStack> context) {
        try {
            CommandSender sender = context.getSource().getSender();
            if(sender instanceof Player player)
                new BoonPlayerGui(player).open();
            else
                sender.sendMessage(SystemMessage.error("Command can only be used by players"));

            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to open boons GUI: {%s}", e.getMessage())));
            return 0;
        }
    }

    private int handleTeams(CommandContext<CommandSourceStack> context) {
        try {
            CommandSender sender = context.getSource().getSender();
            if(sender instanceof Player player)
                new PlayerTeamsGui(player).open();
            else
                sender.sendMessage(SystemMessage.error("Command can only be used by players"));

            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to open teams GUI: {%s}", e.getMessage())));
            return 0;
        }
    }

    /**
     * Handle the /modernmanhunt reload command
     */
    private int handleReload(CommandContext<CommandSourceStack> context) {
        try {
            bartering.load();
            coreConfig.load();
            handicap.load();
            boon.load();

            Core.getInstance().getHandicapManager().reloadRegistry();
            Core.getInstance().getBoonManager().reloadRegistry();

            context.getSource().getSender().sendMessage(
                    SystemMessage.success("ModernManhunt configuration reloaded {successfully}"));

            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to reload configuration: {%s}", e.getMessage())));
            return 0;
        }
    }
}
