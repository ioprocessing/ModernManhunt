package me.Ford.modernManhunt.Commands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import me.Ford.modernManhunt.Seeds;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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

import static me.Ford.modernManhunt.Functions.participantArray;

public class ManhuntCommand implements CommandExecutor, TabExecutor {

    ///  TODO: MAKE THIS CLASS READABLE
    ///

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if ((args.length > 3 && !(args[0].equalsIgnoreCase("handicap") || args[1].equalsIgnoreCase("create"))) || (args.length == 0))
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
                        if (!(Functions.mmRunners.getEntries().contains(p.getName()))) {
                            if (Functions.mmHunters.getEntries().contains(p.getName())) {
                                Functions.mmHunters.removeEntity(p);
                                Functions.hunterArray.remove(p);
                            }
                            Functions.mmRunners.addEntity(p);
                            Functions.runnerArray.add(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully added " + p.getName() + " to the runners!");
                        }
                        else s.sendMessage("§c" + p.getName() + " is already a runner!");
                    }
                    case "remove" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (Functions.mmRunners.getEntries().contains(p.getName())) {
                            Functions.mmRunners.removeEntity(p);
                            Functions.runnerArray.remove(p);
                            participantArray.remove(p);
                            s.sendMessage("§aSuccessfully removed " + p.getName() + " from the runners!");
                        }
                        else s.sendMessage("§c" + p.getName() + " isn't a runner!");
                    }
                    case "list" -> {
                        if (Functions.mmRunners.getEntries().isEmpty())
                            s.sendRawMessage("§cThere are no players currently running!");
                        else {
                            String playerList = String.join(", ", Functions.mmRunners.getEntries());
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
                        if (!(Functions.mmHunters.getEntries().contains(p.getName()))) {
                            if (Functions.mmRunners.getEntries().contains(p.getName())) {
                                Functions.mmRunners.removeEntity(p);
                                Functions.runnerArray.remove(p);
                            }
                            Functions.mmHunters.addEntity(p);
                            Functions.hunterArray.add(p);
                            participantArray.add(p);
                            s.sendMessage("§aSuccessfully added " + p.getName() + " to the hunters!");
                        }
                        else s.sendMessage("§c" + p.getName() + " is already a hunter!");
                    }
                    case "remove" -> {
                        Player p =  Bukkit.getPlayer(args[2]);
                        if (Functions.mmHunters.getEntries().contains(p.getName())) {
                            Functions.mmHunters.removeEntity(p);
                            Functions.hunterArray.remove(p);
                            participantArray.remove(p);
                            s.sendMessage("§aSuccessfully removed " + p.getName() + " from the hunters!");
                        }
                        else s.sendMessage("§c" + p.getName() + " isn't a hunter!");
                    }
                    case "list" -> {
                        if (Functions.mmHunters.getEntries().isEmpty())
                            s.sendRawMessage("§cThere are no players currently hunting!");
                        else {
                            String playerList = String.join(", ", Functions.mmHunters.getEntries());
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
                if (!(Functions.mmRunners.getEntries().isEmpty()) && !(Functions.mmHunters.getEntries().isEmpty())) {
                    for(Player participant : participantArray)
                        if (participant.isDead()) {
                            s.sendRawMessage("§cAll players must be alive to begin!");
                            return true;
                        }
                    // Disable locator bar
                    String worldName = s.getWorld().getName();
                    ArrayList<World> selectWorlds = new ArrayList<>();
                    selectWorlds.add(Bukkit.getWorld(worldName));
                    selectWorlds.add(Bukkit.getWorld(worldName + "_nether"));
                    selectWorlds.add(Bukkit.getWorld(worldName + "_the_end"));
                    for (World world : selectWorlds) {
                        world.setGameRule(GameRule.LOCATOR_BAR, false);
                    }
                    // Initialize title
                    Component mainTitle = Component.text("Get ready!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
                    Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                    // For each hunter,
                    for (String name : Functions.mmHunters.getEntries()) {
                        // Make them healthy + clear their inventory
                        Functions.resetPlayer(name);
                        Player p = Bukkit.getPlayer(name);
                        // If the player is on the handicap list, give them the TP star
                        if (Config.handicapTPList.contains(p.getName()))
                            p.getInventory().setItem(7, CustomItems.tpStar());
                        p.setMetadata("TargetedPlayer", new FixedMetadataValue(ModernManhunt.getInstance(), 0));
                        Title title = Title.title(mainTitle, Component.empty(), times);
                        p.showTitle(title);

                        // If the player's current respawn location isn't in the world the hunt starts in,
                        // set it to the default spawn location in that world
                        if (p.getRespawnLocation() == null || (p.getRespawnLocation().getWorld() != p.getLocation().getWorld()))
                            p.setRespawnLocation(p.getWorld().getSpawnLocation());

                        // And give them a compass
                        ItemStack compass = CustomItems.hunterCompass(p);
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(Functions.runnerArray.getFirst().getLocation(), false);
                        compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.getInventory().setItem(8, compass);
                        p.updateInventory();
                    }
                    // For each runner,
                    for (String name : Functions.mmRunners.getEntries()) {
                        // Make them healthy and mark that they're being hunted
                        Functions.resetPlayer(name);
                        Player p = Bukkit.getPlayer(name);
                        p.setMetadata("BeingHunted", new FixedMetadataValue(ModernManhunt.getInstance(), true));
                        Component runSubtitle = Component.text("Punch a hunter or start running to begin", NamedTextColor.GREEN);
                        Title title = Title.title(mainTitle, runSubtitle, times);
                        p.showTitle(title);

                        p.setMetadata("PVPImmune", new FixedMetadataValue(ModernManhunt.getInstance(), true));

                        Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                            if (p.hasMetadata("PVPImmune"))
                                p.removeMetadata("PVPImmune", ModernManhunt.getInstance());
                        }, 20);

                        if (p.getRespawnLocation() == null || (p.getRespawnLocation().getWorld() != p.getLocation().getWorld()))
                            p.setRespawnLocation(p.getWorld().getSpawnLocation());
                    }
                    for (Player p : s.getWorld().getPlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1f, 0.5f);
                    }
                    s.getWorld().setTime(0);
                } else {
                    s.sendRawMessage("§cYou must have at least one player on both teams to begin!");
                }
            }

            ///  STOP ARGS ///

            case "stop" -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.removeMetadata("BeingHunted",  ModernManhunt.getInstance());
                    if (p.hasMetadata("DeadRunner")) {
                        Functions.exitSpectator(p);
                    }
                }
                s.sendMessage("§cEnded the current manhunt and cleared all tags");
            }

            ///  WORLD ARGS ///

            case "world" -> {
                if (
                        (((args.length == 1) || ((args.length == 2) && !(args[1].equalsIgnoreCase("list")))) || args.length > 4)
                )
                    return false;
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        if (Bukkit.getWorld(args[2]) != null || !StringUtils.isAlphanumeric(args[2]) || (args.length == 4 && !StringUtils.isNumeric(args[3])))
                            return false;
                        Long randomSeed;
                        if (args.length == 3)
                            randomSeed = new ArrayList<>(Seeds.seedSet).get(new Random().nextInt(Seeds.seedSet.size()));
                        else randomSeed = Long.valueOf(args[3]);
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
                        Config.getInstance().add(newOverworld.getName(), "worlds");
                        Config.getInstance().add(newNether.getName(), "worlds");
                        Config.getInstance().add(newTheEnd.getName(), "worlds");

                        s.sendRawMessage("§aWorld " + args[2] + " successfully created!");
                    }

                    case "delete" -> {
                        ArrayList<World> selectedWorlds = Functions.unloadWorlds(args[2]);
                        if (selectedWorlds == null)
                            return false;
                        // For each world to unload,
                        for (World currentWorld : selectedWorlds) {
                            // Check for players and move them elsewhere
                            Functions.checkForPlayers(currentWorld, selectedWorlds);
                            Bukkit.unloadWorld(currentWorld, false);
                            try {
                                FileUtils.deleteDirectory(currentWorld.getWorldFolder());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Config.getInstance().remove(currentWorld.getName(), "worlds");
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
                                Functions.generateEndPlatform(Bukkit.getWorld(args[2]));
                            } else s.teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
                        } else return false;
                    }

                    case "unload" -> {
                        ArrayList<World> selectedWorlds = Functions.unloadWorlds(args[2]);
                        if (selectedWorlds == null)
                            return false;
                        // For each world to unload,
                        for (World currentWorld : selectedWorlds) {
                            // Check for players and move them elsewhere
                            Functions.checkForPlayers(currentWorld, selectedWorlds);
                            Bukkit.unloadWorld(currentWorld, false);
                        }
                        s.sendRawMessage("§aWorld " + args[2] + " successfully unloaded!");
                    }
                }
            }

            /// HELP ARGS ///

            case "help" ->
                s.sendMessage(Component.text("§6> §a/recipe" +
                        "\n§6> §a/mm runner <args>" +
                        "\n§6> §a/mm hunter <args>" +
                        "\n§6> §a/mm handicap <args>" +
                        "\n§6> §a/mm world <args>" +
                        "\n§6> §a/mm start" +
                        "\n§6> §a/mm stop" +
                        "\n§6> §a/mm help" +
                        "\n§6> §a/mm reload"));

            ///  HANDICAP ARGS ///

            case "handicap" -> {
                if (
                        ((args.length == 1) || ((args.length == 2) && !(args[1].equalsIgnoreCase("list"))))
                                || args.length == 3 || ((args.length == 4) && (Bukkit.getPlayer(args[2].toLowerCase()) == null) || args.length > 4)
                )
                    return false;
                switch (args[1].toLowerCase()) {
                    case "add" -> {
                        String p =  args[2];
                        if (args[3].equalsIgnoreCase("tp")) {
                            if (!(Config.handicapTPList.contains(p))) {
                                Config.getInstance().add(p, "handicapTP");
                                s.sendMessage("§aSuccessfully added that player to the TP handicap list!");
                            } else s.sendMessage("§cThat player is already on the TP handicap list!");
                        } else if (args[3].equalsIgnoreCase("armor")) {
                            if (!(Config.handicapArmorList.contains(p))) {
                                Config.getInstance().add(p, "handicapArmor");
                                s.sendMessage("§aSuccessfully added that player to the armor handicap list!");
                            } else s.sendMessage("§cThat player is already on the armor handicap list!");
                        } else return false;
                    }
                    case "remove" -> {
                        String p =  args[2];
                        if (args[3].equalsIgnoreCase("tp")) {
                            if (Config.handicapTPList.contains(p)) {
                                Config.getInstance().remove(p, "handicapTP");
                                s.sendMessage("§aSuccessfully removed that player from the TP handicap list!");
                            } else s.sendMessage("§cThat player isn't on the TP handicap list!");
                        } else if (args[3].equalsIgnoreCase("armor")) {
                            if (Config.handicapArmorList.contains(p)) {
                                Config.getInstance().remove(p, "handicapArmor");
                                s.sendMessage("§aSuccessfully removed that player from the armor handicap list!");
                            } else s.sendMessage("§cThat player isn't on the armor handicap list!");
                        } else return false;
                    }
                    case "list" -> {
                        if (Config.handicapTPList.isEmpty() && Config.handicapArmorList.isEmpty())
                            s.sendRawMessage("§cThere are no players on the handicap list!");
                        else {
                            if (!Config.handicapTPList.isEmpty()) {
                                String playerTPList = String.join(", ", Config.handicapTPList);
                                s.sendRawMessage("§aTP Handicap players: " + playerTPList);
                            }
                            if (!Config.handicapArmorList.isEmpty()) {
                                String playerArmorList = String.join(", ", Config.handicapArmorList);
                                s.sendRawMessage("§aArmor Handicap players: " + playerArmorList);
                            }
                        }
                    }
                    default -> {
                        return false;
                    }
                }
            }

            ///  RELOAD ARGS ///

            case "reload" -> {
                HandlerList.unregisterAll(ModernManhunt.getInstance());
                ModernManhunt.getInstance().register(ModernManhunt.getInstance());
                s.sendRawMessage("§aReloaded ModernManhunt!");
            }

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
                return Arrays.asList("runner", "hunter", "stop", "start", "world", "help", "handicap", "reload");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("runner") ||  args[0].equalsIgnoreCase("hunter") || args[0].equalsIgnoreCase("handicap")) {
                    return Arrays.asList("list", "add", "remove");
                }
                else if (args[0].equalsIgnoreCase("world")) {
                    return Arrays.asList("list", "create", "delete", "tp", "unload");
                }
                else return new ArrayList<>();
            }
            case 3 -> {
                if ((args[0].equalsIgnoreCase("runner") || args[0].equalsIgnoreCase("hunter") || args[0].equalsIgnoreCase("handicap"))
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
            case 4 -> {
                if (args[0].equalsIgnoreCase("handicap") && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    return Arrays.asList("tp", "armor");
                }
            }
        }
        return new ArrayList<>();
    }
}