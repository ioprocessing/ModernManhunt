package org.ioprocessing.modernManhunt.commands.subcommands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.teams.Spectator;

import java.util.ArrayList;
import java.util.List;

public class SpectatorCommand {

    /**
     * Handle the /mm spectator add command
     * Adds a player to the spectator team and removes them from the other teams
     */
    public int handleAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        List<Player> players = resolveSelector(context);

        if (players.isEmpty()) {
            sender.sendMessage(SystemMessage.error("No players matched that selector"));
            return 0;
        }

        try {
            for (Player player : players) {
                if (!Spectator.isSpectating(player)) {
                    addSpectator(sender, player);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is already a spectator", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to add player to spectator team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm spectator remove command
     * Removes a player from the spectator team
     */
    public int handleRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        List<Player> players = resolveSelector(context);

        if (players.isEmpty()) {
            sender.sendMessage(SystemMessage.error("No players matched that selector"));
            return 0;
        }

        try {
            for (Player player : players) {
                if (Spectator.isSpectating(player)) {
                    removeSpectator(sender, player);
                }
                else sender.sendMessage(SystemMessage.error(String.format("{%s} is not a spectator", player.getName())));
            }
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to remove player from the spectator team: {%s}", e.getMessage())));
            return 0;
        }
        return 1;
    }

    /**
     * Handle the /mm spectator list command
     * Sends a list of all players on the spectator team
     */
    public int handleList(CommandContext<CommandSourceStack> context) {
        try {
            List<String> spectatorNames = new ArrayList<>();
            for(Player spectator : Core.getInstance().getTeamManager().getSpectators())
                spectatorNames.add(spectator.getName());

            String playerList = String.join(", ", spectatorNames);

            if(spectatorNames.isEmpty()) {
                context.getSource().getSender().sendMessage(SystemMessage.error("There are currently no spectators"));
                return 1;
            }

            context.getSource().getSender().sendMessage(SystemMessage.info(String.format("Spectators: {%s}", playerList)));
            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to fetch list of spectators: {%s}", e.getMessage())));
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

    private void addSpectator(CommandSender sender, Player target) {
        if(Core.getInstance().isActiveManhunt())
            Spectator.startSpectating(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully added {%s} to the spectator team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been added to the {spectator} team"));
    }

    private void removeSpectator(CommandSender sender, Player target) {
        Spectator.stopSpectating(target);

        sender.sendMessage(SystemMessage.success(String.format("Successfully removed {%s} from the spectator team", target.getName())));
        target.sendMessage(SystemMessage.info("You have been removed from the {spectator} team"));
    }
}

