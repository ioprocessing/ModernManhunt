package me.Ford.modernManhunt;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Records.BarteringDrops;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.codehaus.plexus.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Functions {

    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static Scoreboard board = manager.getNewScoreboard();
    public static Team spectatorTeam;

    public static int random(int max, int min) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    public static void openRecipesMenu(Player p) {
        Inventory i = Bukkit.createInventory(p, 9, Component.text("Recipes"));

        i.setItem(0, CustomItems.primedPickaxe());
        i.setItem(1, CustomItems.strengthenedSword());
        i.setItem(2, CustomItems.bolsteredBow());
        i.setItem(3, new ItemStack(Material.TRIDENT));
        i.setItem(4, CustomItems.compactAnvil());
        i.setItem(5, CustomItems.bundledArrows());
        i.setItem(6, CustomItems.goldenHead());
        i.setItem(7, CustomItems.loyaltyBook());

        p.openInventory(i);
        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(),  "Recipes Menu"));
    }

    public static Player currentTarget(Player hunter) {
        List<MetadataValue> metadata = hunter.getMetadata("TargetedPlayer");
        return ManhuntCommand.runnerArray.get(metadata.getFirst().asInt());
    }

    public static void generateEndPlatform(World world) {
        // Clear area for obsidian platform
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block block = world.getBlockAt(x + 98, 48, z - 2);
                if (block.getType() == Material.OBSIDIAN) continue;
                block.breakNaturally(false, true);
                block.setType(Material.OBSIDIAN);
            }
        }

        // Clear area for player
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int y = 0; y < 3; y++) {
                    world.getBlockAt(x + 98, 49 + y, z - 2).breakNaturally(false, true);
                }
            }
        }
    }

    public static Team getNoCollisionTeam() {
        if (spectatorTeam == null) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            spectatorTeam = board.getTeam("spectators");
            if (spectatorTeam == null) {
                spectatorTeam = board.registerNewTeam("spectators");
                spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                spectatorTeam.setAllowFriendlyFire(false);
                spectatorTeam.setCanSeeFriendlyInvisibles(true);
            }
        }
        return spectatorTeam;
    }

    public static void enterSpectator(Player p) {
            p.setGameMode(GameMode.ADVENTURE);
            p.setAllowFlight(true);
            getNoCollisionTeam().addPlayer(p);
            // Hide the spectator from all players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasMetadata("DeadRunner"))
                    onlinePlayer.hidePlayer(ModernManhunt.getInstance(), p);
            }
        }

    public static void exitSpectator(Player p) {
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                onlinePlayers.showPlayer(ModernManhunt.getInstance(), p);
            }
            getNoCollisionTeam().removePlayer(p);
            p.setGameMode(GameMode.SURVIVAL);
            p.clearActivePotionEffects();
            p.removeMetadata("DeadRunner", ModernManhunt.getInstance());
            p.setAllowFlight(false);
            p.getInventory().clear();
    }

    public static ArrayList<World> unloadWorlds(String worldName) {
        if (!StringUtils.isAlphanumeric(worldName) || (worldName.endsWith("_nether") ||  worldName.endsWith("_the_end")))
            return null;
        ArrayList<String> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            worlds.add(world.getName());
        }
        if (worlds.contains(worldName)) {
            ArrayList<World> selectWorlds = new ArrayList<>();
            selectWorlds.add(Bukkit.getWorld(worldName));
            selectWorlds.add(Bukkit.getWorld(worldName + "_nether"));
            selectWorlds.add(Bukkit.getWorld(worldName + "_the_end"));
            return selectWorlds;
        } else return null;
    }

    public static void checkForPlayers(World world, ArrayList<World> selectedWorlds) {
        if (!world.getPlayers().isEmpty()) {
            // Get a list of all worlds
            List<World> safeWorlds = Bukkit.getWorlds();
            for (World unsafeWorld : selectedWorlds) {
                // And remove the current set of worlds marked for deletion
                safeWorlds.remove(unsafeWorld);
            }
            // Then teleport every player in the unempty world to a safe world
            for (Player p : world.getPlayers()) {
                p.teleport(safeWorlds.getFirst().getSpawnLocation());
            }
        }
    }

    public static void resetPlayer(String name) {
        Player p = Bukkit.getPlayer(name);
        p.getInventory().clear();
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext())
        {
            AdvancementProgress progress = p.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria())
                progress.revokeCriteria(criteria);
        }
        p.setExperienceLevelAndProgress(0);
        p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
    }

    public static void consumeGoldenHead (Player p, ItemStack head) {

        // If item is on cooldown, do nothing

        if (p.getCooldown(Keys.HEAD_GROUP) > 0) {
            return;
        }

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

        // Then add the potion effects and cooldown

        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 3));
        p.setCooldown(Keys.HEAD_GROUP, 20);

        // Lower stack count
        Bukkit.getScheduler().runTask(ModernManhunt.getInstance(), () ->
            head.setAmount(head.getAmount() - 1));
    }

    public static void consumePlayerHead (Player p, ItemStack head) {

        // If item is on cooldown, do nothing

        if (p.getCooldown(Keys.HEAD_GROUP) > 0) {
            return;
        }

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

        // Then add the potion effects and cooldown

        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 5, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
        p.setCooldown(Keys.HEAD_GROUP, 20);

        // Lower stack count
        Bukkit.getScheduler().runTask(ModernManhunt.getInstance(), () ->
                head.setAmount(head.getAmount() - 1));
    }

    public static void gameEnd(String winnerMessage, NamedTextColor color) {
        // Call this when the game ends
        Component mainTitle = Component.text(winnerMessage, color).decoration(TextDecoration.BOLD, true);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
        Title title = Title.title(mainTitle, Component.empty(), times);
        for (Player participant : ManhuntCommand.participantArray) {
            participant.showTitle(title);
            participant.playSound(participant.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.3f, 1f);
            participant.removeMetadata("DeadRunner", ModernManhunt.getInstance());
        }
    }

    public static ItemStack getCustomBarter() {
        int totalWeight = Collections.CUSTOM_BARTER_POOL.stream().mapToInt(BarteringDrops::weight).sum();
        int roll = random(totalWeight, 1);
        int current = 0;

        for (BarteringDrops drop : Collections.CUSTOM_BARTER_POOL) {
            current += drop.weight();
            if (roll <= current) {
                return drop.generator().get();
            }
        }

        // Fallback
        return new ItemStack(Material.AIR);
    }
}
