package org.ioprocessing.modernManhunt.commands.subcommands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.ArrayList;
import java.util.List;

public class RunnerCommand {

    /**
     * Handle the /mm runner add command
     * Adds a player to the running team and removes them from the other teams
     */
    public int handleAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        CommandSender sender = context.getSource().getSender();
        List<Player> players = resolveSelector(context);

        if (players.isEmpty()) {
            sender.sendMessage(SystemMessage.error("No players matched that selector"));
            return 0;
        }

        try {
            for (Player player : players) {
                if (!teamManager.isRunnerTeam(player)) {
                    addRunner(sender, player, teamManager);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is already a runner", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to add player to running team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm runner remove command
     * Removes a player from the running team
     */
    public int handleRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        CommandSender sender = context.getSource().getSender();
        List<Player> players = resolveSelector(context);

        if (players.isEmpty()) {
            sender.sendMessage(SystemMessage.error("No players matched that selector"));
            return 0;
        }

        try {
            for (Player player : players) {
                if (teamManager.isRunnerTeam(player)) {
                    removeRunner(sender, player, teamManager);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is not a runner", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to remove player from the running team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm runner list command
     * Sends a list of all players on the running team
     */
    public int handleList(CommandContext<CommandSourceStack> context) {
        try {
            List<String> runnerNames = new ArrayList<>();
            for(Player runner : Core.getInstance().getTeamManager().getRunners())
                runnerNames.add(runner.getName());

            String playerList = String.join(", ", runnerNames);

            if(runnerNames.isEmpty()) {
                context.getSource().getSender().sendMessage(SystemMessage.error("There are currently no runners"));
                return 1;
            }

            context.getSource().getSender().sendMessage(SystemMessage.info(String.format("Runners: {%s}", playerList)));
            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to fetch list of runners: {%s}", e.getMessage())));
            return 0;
        }
    }

    /**
     * Get target players from selector or player name
     */
    private List<Player> resolveSelector(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {

        PlayerSelectorArgumentResolver resolver =
                context.getArgument("player", PlayerSelectorArgumentResolver.class);

        return resolver.resolve(context.getSource());
    }

    private void addRunner(CommandSender sender, Player target, TeamManager teamManager) {
        teamManager.setRunnerTeam(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully added {%s} to the running team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been added to the {runner} team"));
    }

    private void removeRunner(CommandSender sender, Player target, TeamManager teamManager) {
        teamManager.removeRunner(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully removed {%s} from the running team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been removed from the {runner} team"));
    }
}
