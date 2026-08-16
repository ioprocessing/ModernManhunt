package org.ioprocessing.modernManhunt.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.gamestate.ManhuntManager;
import org.ioprocessing.modernManhunt.gamestate.ManhuntState;
import org.ioprocessing.modernManhunt.teams.TeamManager;
import org.ioprocessing.modernManhunt.worlds.SeedLoader;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartStopCommand {
    private final ConfigManager<CoreConfig> coreConfig;

    public StartStopCommand(ConfigManager<CoreConfig> coreConfig) {
        this.coreConfig = coreConfig;
    }

    /**
     * Handle the /mm start command
     * Generates a new world, then sends the runners there to let them scout
     */
    public int startManhunt(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        TeamManager teamManager = Core.getInstance().getTeamManager();
        ManhuntManager manhuntManager = Core.getInstance().getManhuntManager();

        if (teamManager.getRunners().isEmpty() || teamManager.getHunters().isEmpty()) {
            sender.sendMessage(SystemMessage.error("Both teams must have at least one player to begin"));
            return 0;
        }

        if (manhuntManager.getState() == ManhuntState.WAITING_FOR_TELEPORT) {
            sender.sendMessage(SystemMessage.error("Cannot start a new Manhunt until all players have been teleported"));
            return 0;
        }

        if(isAnyDead()) {
            sender.sendMessage(SystemMessage.error("All players must be alive to start the Manhunt"));
            return 0;
        }

        Long seed = null;
        try {
            String input = StringArgumentType.getString(context, "seed");
            try {
                seed = Long.parseLong(input);
            } catch (NumberFormatException e) {
                seed = (long) input.hashCode();
            }
        } catch (IllegalArgumentException ignored) {
            int size = SeedLoader.seedSet.size();
            int item = new Random().nextInt(size);
            int i = 0;
            for (Long randomSeed : SeedLoader.seedSet) {
                if (i == item) { seed = randomSeed; break; }
                i++;
            }
        }

        if (seed == null) {
            sender.sendMessage(SystemMessage.error("Failed to generate seed"));
            return 0;
        }

        try {
            deleteManhuntWorlds();

            World newOverworld = WorldCreator.ofKey(Core.MANHUNT_OVERWORLD)
                    .environment(World.Environment.NORMAL).seed(seed).createWorld();
            World newNether = WorldCreator.ofKey(Core.MANHUNT_NETHER)
                    .environment(World.Environment.NETHER).seed(seed).createWorld();
            World newTheEnd = WorldCreator.ofKey(Core.MANHUNT_THE_END)
                    .environment(World.Environment.THE_END).seed(seed).createWorld();

            Core.getManhuntWorlds().clear();

            Core.getManhuntWorlds().add(newOverworld);
            Core.getManhuntWorlds().add(newNether);
            Core.getManhuntWorlds().add(newTheEnd);

            Difficulty difficulty = difficultyFromString(coreConfig.getConfig().getDefaultDifficulty());

            CoreConfig.ManhuntSpawnWorld manhuntSpawnWorld = coreConfig.getConfig().getManhuntSpawnWorld();

            if(manhuntSpawnWorld.isEnabled() && newOverworld != null) {
                Bukkit.getServer().setRespawnWorld(newOverworld);
                newOverworld.setGameRule(GameRules.RESPAWN_RADIUS, manhuntSpawnWorld.getManhuntSpawnRadius());
            }

            // Disable time passing in world until manhunt starts
            for(World world : Core.getManhuntWorlds()) {
                world.setDifficulty(difficulty);
                world.setGameRule(GameRules.ADVANCE_TIME, false);
                if (!coreConfig.getConfig().isLocatorBarEnabled())
                    world.setGameRule(GameRules.LOCATOR_BAR, false);
            }

            manhuntManager.startScoutingPhase(newOverworld);

            sender.sendMessage(SystemMessage.success("Created world and entered {scouting} phase"));
            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to start the Manhunt: {%s}", e.getMessage())));
            return 0;
        }
    }


    public int stopManhunt(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ManhuntManager manhuntManager = Core.getInstance().getManhuntManager();
        try {
            deleteManhuntWorlds();
            manhuntManager.endManhunt(0);
            sender.sendMessage(SystemMessage.success("Stopped Manhunt and deleted world"));
            return 1;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to stop the Manhunt: {%s}", e.getMessage())));
            return 0;
        }
    }

    private void deleteManhuntWorlds() {
        List<NamespacedKey> keys = List.of(Core.MANHUNT_OVERWORLD, Core.MANHUNT_NETHER, Core.MANHUNT_THE_END);

        List<World> worlds = keys.stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        movePlayersFromWorlds(worlds);

        for (World world : worlds) {
            File folder = world.getWorldFolder();
            if (!Bukkit.unloadWorld(world, false)) {
                throw new RuntimeException("Failed to unload world: " + world.getKey());
            }
            try {
                deleteWorldFolder(folder);
                Core.logger().info("Deleted Manhunt dimension: {}", folder.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete dimension folder: " + world.getKey(), e);
            }
        }

        Core.getManhuntWorlds().clear();
    }

    private void deleteWorldFolder(File folder) throws IOException {
        if (!folder.exists()) return;
        try (var stream = java.nio.file.Files.walk(folder.toPath())) {
            stream.sorted(Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(File::delete);
        }
    }

    private void movePlayersFromWorlds(List<World> unsafeWorlds) {
        World safeWorld = null;
        for (World world : Bukkit.getWorlds()) {
            if (!unsafeWorlds.contains(world) && world.getEnvironment() == World.Environment.NORMAL) {
                safeWorld = world;
                break;
            }
        }
        if (safeWorld == null) {
            throw new RuntimeException("No safe world found to teleport players to");
        }
        for (World unsafeWorld : unsafeWorlds) {
            if (unsafeWorld == null) continue;
            for (Player player : unsafeWorld.getPlayers()) {
                player.teleport(safeWorld.getSpawnLocation().toHighestLocation());
            }
        }
    }

    private boolean isAnyDead() {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        return Stream.concat(teamManager.getHunters().stream(), teamManager.getRunners().stream())
                .anyMatch(Player::isDead);
    }

    private Difficulty difficultyFromString(String difficultyString) {
        return switch(difficultyString) {
            case "EASY" -> Difficulty.EASY;
            case "NORMAL" -> Difficulty.NORMAL;
            case "HARD" -> Difficulty.HARD;
            default -> Difficulty.EASY;
        };
    }
}
