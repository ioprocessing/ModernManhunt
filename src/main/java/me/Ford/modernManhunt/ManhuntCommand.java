package me.Ford.modernManhunt;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ManhuntCommand implements CommandExecutor, TabExecutor {

    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static Scoreboard board = manager.getNewScoreboard();
    public static Team mmRunners = board.registerNewTeam("mmRunners");
    public static Team mmHunters = board.registerNewTeam("mmHunters");
    public static ArrayList<Player> runnerArray = new ArrayList<>();
    public static ArrayList<Player> participantArray = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if ((args.length > 3) || (args.length == 0))
            return false;

        Player s = (Player) sender;

        switch (args[0].toLowerCase()) {

            ///  RUNNER ARGS ///

            case "runner" -> {
                if (
                        ((args.length == 1) || ((args.length == 2) && !(args[1].equalsIgnoreCase("list"))))
                                || ((args.length == 3) && (Bukkit.getPlayer(args[2].toLowerCase()) == null))
                )
                    return false;
                switch (args[1].toLowerCase()) {
                    case "add" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (!(mmRunners.getEntries().contains(p.getName()))) {
                            if (mmHunters.getEntries().contains(p.getName()))
                                mmHunters.removeEntity(p);
                            mmRunners.addEntity(p);
                            runnerArray.add(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully added that player to the runners!");
                        }
                        else s.sendMessage("§cThat player is already a runner!");
                    }
                    case "remove" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (mmRunners.getEntries().contains(p.getName())) {
                            mmRunners.removeEntity(p);
                            runnerArray.remove(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully removed that player from the runners!");
                        }
                        else s.sendMessage("§cThat player isn't a runner!");
                    }
                    case "list" -> {
                        if (mmRunners.getEntries().isEmpty())
                            s.sendRawMessage("§cThere are no players currently running!");
                        else {
                            String playerList = String.join(", ", mmRunners.getEntries());
                            s.sendRawMessage("§aRunners: " + playerList);
                        }
                    }
                    default -> {
                        return false;
                    }
                }
            }

            ///  HUNTER ARGS ///

            case "hunter" -> {
                if (
                        ((args.length == 1) || ((args.length == 2) && !(args[1].equalsIgnoreCase("list"))))
                                || ((args.length == 3) && (Bukkit.getPlayer(args[2].toLowerCase()) == null))
                )
                    return false;
                switch (args[1].toLowerCase()) {
                    case "add" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (!(mmHunters.getEntries().contains(p.getName()))) {
                            if (mmRunners.getEntries().contains(p.getName()))
                                mmRunners.removeEntity(p);
                            mmHunters.addEntity(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully added that player to the hunters!");
                        }
                        else s.sendMessage("§cThat player is already a hunter!");
                    }
                    case "remove" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (mmHunters.getEntries().contains(p.getName())) {
                            mmHunters.removeEntity(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully removed that player from the hunters!");
                        }
                        else s.sendMessage("§cThat player isn't a hunter!");
                    }
                    case "list" -> {
                        if (mmHunters.getEntries().isEmpty())
                            s.sendRawMessage("§cThere are no players currently hunting!");
                        else {
                            String playerList = String.join(", ", mmHunters.getEntries());
                            s.sendRawMessage("§aHunters: " + playerList);
                        }
                    }
                    default -> {
                        return false;
                    }
                }
            }

            ///  START ARGS ///

            case "start" -> {
                if (!(mmRunners.getEntries().isEmpty()) && !(mmHunters.getEntries().isEmpty())) {
                    // Initialize title
                    Component mainTitle = Component.text("Get ready!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
                    Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                    // For each hunter,
                    for (String name : mmHunters.getEntries()) {
                        Player p = Bukkit.getPlayer(name);
                        // Make them healthy + clear their inventory
                        p.getInventory().clear();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " everything");
                        p.setExperienceLevelAndProgress(0);
                        p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
                        p.setFoodLevel(20);
                        p.setSaturation(20.0f);
                        p.setMetadata("TargetedPlayer", new FixedMetadataValue(ModernManhunt.getInstance(), 0));
                        Title title = Title.title(mainTitle, Component.empty(), times);
                        p.showTitle(title);

                        // If the player's current respawn location isn't in the world the hunt starts in,
                        // set it to the default spawn location in that world
                        if (p.getRespawnLocation().getWorld() != p.getLocation().getWorld())
                            p.setRespawnLocation(p.getWorld().getSpawnLocation());

                        // And give them a compass
                        ItemStack compass = MMFunctions.HunterCompass(p);
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.getFirst().getLocation(), false);
                        compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.getInventory().setItem(8, compass);
                        p.updateInventory();
                    }
                    // For each runner,
                    for (String name : mmRunners.getEntries()) {
                        // Make them healthy and mark that they're being hunted
                        Player p = Bukkit.getPlayer(name);
                        p.getInventory().clear();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " everything");
                        p.setExperienceLevelAndProgress(0);
                        p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
                        p.setFoodLevel(20);
                        p.setSaturation(20.0f);
                        p.setMetadata("BeingHunted", new FixedMetadataValue(ModernManhunt.getInstance(), true));
                        Component runSubtitle = Component.text("Punch a hunter or start running to begin", NamedTextColor.GREEN);
                        Title title = Title.title(mainTitle, runSubtitle, times);
                        p.showTitle(title);

                        if (p.getRespawnLocation().getWorld() != p.getLocation().getWorld())
                            p.setRespawnLocation(p.getWorld().getSpawnLocation());
                    }
                    for (Player p : s.getWorld().getPlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1f, 0.5f);
                    }
                } else {
                    s.sendRawMessage("§cYou must have at least one player on both teams to begin!");
                }
            }

            ///  STOP ARGS ///

            case "stop" -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.removeMetadata("BeingHunted",  ModernManhunt.getInstance());
                    if (p.hasMetadata("DeadRunner")) {
                        MMFunctions.ExitSpectator(p);
                    }
                }
                s.sendMessage("§cEnded the current manhunt and cleared all tags");
            }

            ///  WORLD ARGS ///

            case "world" -> {
                if (
                        ((args.length == 1) || ((args.length == 2) && !(args[1].equalsIgnoreCase("list"))))
                )
                    return false;
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        if ( !(Bukkit.getWorld(args[2]) == null) || !StringUtils.isAlphanumeric(args[2]))
                            return false;
                        Long randomSeed = new ArrayList<>(Resources.seedSet)
                                .get(new Random().nextInt(Resources.seedSet.size()));

                        // OVERWORLD

                        WorldCreator wcOverworld = new WorldCreator(args[2]);
                        wcOverworld.environment(World.Environment.NORMAL);
                        wcOverworld.seed(randomSeed);

                        // NETHER

                        WorldCreator wcNether = new WorldCreator(args[2] + "_nether");
                        wcNether.environment(World.Environment.NETHER);
                        wcNether.seed(randomSeed);

                        // THE END

                        WorldCreator wcTheEnd = new WorldCreator(args[2] + "_the_end");
                        wcTheEnd.environment(World.Environment.THE_END);
                        wcTheEnd.seed(randomSeed);

                        World newOverworld = wcOverworld.createWorld();
                        World newNether = wcNether.createWorld();
                        World newTheEnd = wcTheEnd.createWorld();
                        Worlds.getInstance().add(newOverworld.getName());
                        Worlds.getInstance().add(newNether.getName());
                        Worlds.getInstance().add(newTheEnd.getName());

                        s.sendRawMessage("§aWorld " + args[2] + " successfully created!");
                    }

                    case "delete" -> {
                        ArrayList<World> selectedWorlds = MMFunctions.unloadWorlds(args[2]);
                        if (selectedWorlds == null)
                            return false;
                        // For each world to unload,
                        for (World currentWorld : selectedWorlds) {
                            // Check for players and move them elsewhere
                            MMFunctions.checkForPlayers(currentWorld, selectedWorlds);
                            Bukkit.unloadWorld(currentWorld, false);
                            try {
                                FileUtils.deleteDirectory(currentWorld.getWorldFolder());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Worlds.getInstance().remove(currentWorld.getName());
                        }
                        s.sendRawMessage("§aWorld " + args[2] + " successfully deleted!");
                    }

                    case "list" -> {
                        ArrayList<String> worlds = new ArrayList<>();
                        for (World world : Bukkit.getWorlds()) {
                            if (world.getEnvironment() == World.Environment.NORMAL)
                                worlds.add(world.getName());
                        }
                        String worldList = String.join(", ", worlds);
                        s.sendRawMessage("§aWorlds: " + worldList);
                    }

                    case "tp" -> {
                        ArrayList<String> worlds = new ArrayList<>();
                        for (World world : Bukkit.getWorlds()) {
                            worlds.add(world.getName());
                        }
                        if (worlds.contains(args[2])) {
                            if (args[2].endsWith("_the_end")) {
                                s.teleport(new Location(Bukkit.getWorld(args[2]), 100.5, 49, 0.5, 90.0f, s.getPitch()));
                                MMFunctions.GenerateEndPlatform(Bukkit.getWorld(args[2]));
                            } else s.teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
                        } else return false;
                    }

                    case "unload" -> {
                        ArrayList<World> selectedWorlds = MMFunctions.unloadWorlds(args[2]);
                        if (selectedWorlds == null)
                            return false;
                        // For each world to unload,
                        for (World currentWorld : selectedWorlds) {
                            // Check for players and move them elsewhere
                            MMFunctions.checkForPlayers(currentWorld, selectedWorlds);
                            Bukkit.unloadWorld(currentWorld, false);
                        }
                        s.sendRawMessage("§aWorld " + args[2] + " successfully unloaded!");
                    }
                }
            }

            /// HELP ARGS ///

            case "help" ->
                s.sendMessage(Component.text("§6> §a/recipes" +
                        "\n§6> §a/mm [runner/hunter] [add/remove] <player name>" +
                        "\n§6> §a/mm [runner/hunter] list" +
                        "\n§6> §a/mm world [create/delete/tp] <world name>" +
                        "\n§6> §a/mm world list" +
                        "\n§6> §a/mm start" +
                        "\n§6> §a/mm stop" +
                        "\n§6> §a/mm help"));

            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        switch (args.length) {
            case 1 -> {
                return Arrays.asList("runner", "hunter", "stop", "start", "world", "help");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("runner") ||  args[0].equalsIgnoreCase("hunter")) {
                    return Arrays.asList("list", "add", "remove");
                }
                else if (args[0].equalsIgnoreCase("world")) {
                    return Arrays.asList("list", "create", "delete", "tp", "unload");
                }
                else return new ArrayList<>();
            }
            case 3 -> {
                if ((args[0].equalsIgnoreCase("runner") ||  args[0].equalsIgnoreCase("hunter"))
                        && (args[1].equalsIgnoreCase("add") ||  args[1].equalsIgnoreCase("remove"))) {
                    return null;
                } else if ((args[0].equalsIgnoreCase("world"))
                        && args[1].equalsIgnoreCase("tp")) {
                    ArrayList<String> worlds = new ArrayList<>();
                    for (World world : Bukkit.getWorlds()) {worlds.add(world.getName());}
                    return worlds;
                } else if ((args[0].equalsIgnoreCase("world"))
                        && (args[1].equalsIgnoreCase("delete") ||  args[1].equalsIgnoreCase("unload"))) {
                    ArrayList<String> worlds = new ArrayList<>();
                    for (World world : Bukkit.getWorlds()) {
                        if (world.getEnvironment() == World.Environment.NORMAL)
                            worlds.add(world.getName());
                    }
                    return worlds;
                }
            }
        }
        return new ArrayList<>();
    }
}