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

public class HunterCommand {

    /**
     * Handle the /mm hunter add command
     * Adds a player to the hunting team and removes them from the other teams
     */
    public int handleSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        CommandSender sender = context.getSource().getSender();
        List<Player> players = resolveSelector(context);

        if (players.isEmpty()) {
            sender.sendMessage(SystemMessage.error("No players matched that selector"));
            return 0;
        }

        try {
            for (Player player : players) {
                if (!teamManager.isHunterTeam(player)) {
                    addHunter(sender, player, teamManager);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is already a hunter", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to add player to hunting team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm hunter remove command
     * Removes a player from the hunting team
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
                if (teamManager.isHunterTeam(player)) {
                    removeHunter(sender, player, teamManager);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is not a hunter", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to remove player from the hunting team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm hunter list command
     * Sends a list of all players on the hunting team
     */
    public int handleList(CommandContext<CommandSourceStack> context) {
        try {
            List<String> hunterNames = new ArrayList<>();
            for(Player hunter : Core.getInstance().getTeamManager().getHunters())
                hunterNames.add(hunter.getName());

            String playerList = String.join(", ", hunterNames);

            if(hunterNames.isEmpty()) {
                context.getSource().getSender().sendMessage(SystemMessage.error("There are currently no hunters"));
                return 1;
            }

            context.getSource().getSender().sendMessage(SystemMessage.info(String.format("Hunters: {%s}", playerList)));
            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to fetch list of hunters: {%s}", e.getMessage())));
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

    private void addHunter(CommandSender sender, Player target, TeamManager teamManager) {
        teamManager.setHunterTeam(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully added {%s} to the hunting team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been added to the {hunter} team"));
    }

    private void removeHunter(CommandSender sender, Player target, TeamManager teamManager) {
        teamManager.removeHunter(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully removed {%s} from the hunting team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been removed from the {hunter} team"));
    }
}
