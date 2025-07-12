package me.Ford.modernManhunt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MMFunctions {

    public static int random(int max, int min) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    public static ItemStack ConsumablePlayerHead(Player p) {

        // Create the player head
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        // Name the head after the player and style it
        TextComponent head_name = Component.text(p.getName() + "'s Head", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Speed II (10s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Saturation I (0.5s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("used to craft ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Golden Heads").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(".").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build()));


        meta.customName(head_name);
        meta.setOwningPlayer(p);

        // Add custom tag that we'll check for later to consume the head
        meta.getPersistentDataContainer().set(Keys.CONSUMABLE_HEAD, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(Keys.HEAD_GROUP);
        meta.setUseCooldown(cd);

        // Set the item meta and add it to the loot dropped
        playerHead.setItemMeta(meta);
        return playerHead;
    }

    public static ItemStack GoldenHead() {

        // Create ghead ItemStack
        ItemStack goldenHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) goldenHead.getItemMeta();

        // Now give it the custom item properties
        TextComponent head_name = Component.text("Golden Head", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Saturation I (0.5s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("Speed II (15s), Absorption I (120s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("and Regeneration IV (5s).").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(head_name);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("golden_head"));
        meta.setCustomModelDataComponent(model);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.GOLDEN_HEAD, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(Keys.HEAD_GROUP);
        meta.setUseCooldown(cd);

        // Set the item meta
        goldenHead.setItemMeta(meta);

        return goldenHead;
    }

    public static ItemStack PrimedPickaxe() {

        // Create primed pickaxe ItemStack
        ItemStack primePick = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = primePick.getItemMeta();

        // Now give it the custom item properties
        TextComponent pick_name = Component.text("Primed Pickaxe", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.EFFICIENCY, 2, false);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.lore(Arrays.asList(
                Component.text("A heated pickaxe that automatically").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("smelts certain types of blocks.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(pick_name);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("primed_pickaxe"));
        meta.setCustomModelDataComponent(model);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.PRIMED_PICK, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Custom tag that tracks accumulated experience points
        meta.getPersistentDataContainer().set(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT, 0f);

        // Set the item meta
        primePick.setItemMeta(meta);
        return primePick;
    }

    public static ItemStack StrengthenedSword() {

        // Create strengthened sword ItemStack
        ItemStack strengthSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = strengthSword.getItemMeta();

        // Now give it the custom item properties
        TextComponent sword_name = Component.text("Strengthened Sword", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.SHARPNESS, 1, false);
        meta.lore(Arrays.asList(
                Component.text("An iron blade that has").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("been tempered and honed.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(sword_name);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("strengthened_sword"));
        meta.setCustomModelDataComponent(model);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.STRENGTH_SWORD, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Set the item meta
        strengthSword.setItemMeta(meta);
        return strengthSword;
    }

    public static ItemStack BolsteredBow() {

        // Create bolstered bow ItemStack
        ItemStack bolsterBow = new ItemStack(Material.BOW);
        ItemMeta meta = bolsterBow.getItemMeta();

        // Now give it the custom item properties
        TextComponent bow_name = Component.text("Bolstered Bow", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.POWER, 1, false);
        meta.lore(Arrays.asList(
                Component.text("A powerful bow with an").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("increased draw weight.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(bow_name);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("bolstered_bow"));
        meta.setCustomModelDataComponent(model);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.BOLSTER_BOW, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Set the item meta
        bolsterBow.setItemMeta(meta);
        return bolsterBow;
    }

    public static ItemStack BundledArrows() {
        ItemStack bundledArrows = new ItemStack(Material.ARROW);
        ItemMeta meta = bundledArrows.getItemMeta();

        // Now give it the custom item properties
        TextComponent bundle_name = Component.text("Bundle of Arrows", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text("A more efficient method of").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("crafting arrows in bulk.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(bundle_name);
        bundledArrows.setAmount(20);

        // Set the item meta
        bundledArrows.setItemMeta(meta);
        return bundledArrows;
    }

    public static ItemStack CompactAnvil() {
        ItemStack compactAnvil = new ItemStack(Material.ANVIL);
        ItemMeta meta = compactAnvil.getItemMeta();

        // Now give it the custom item properties
        TextComponent anvil_name = Component.text("Compact Anvil", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text("A more compact, yet still").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("effective cast-iron anvil.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(anvil_name);

        // Set the item meta
        compactAnvil.setItemMeta(meta);
        return compactAnvil;
    }

    public static void OpenRecipesMenu(Player p) {
        Inventory i = Bukkit.createInventory(p, 9, Component.text("Recipes"));

        i.setItem(0, MMFunctions.PrimedPickaxe());
        i.setItem(1, MMFunctions.StrengthenedSword());
        i.setItem(2, MMFunctions.BolsteredBow());
        i.setItem(3, new ItemStack(Material.TRIDENT));
        i.setItem(4, MMFunctions.CompactAnvil());
        i.setItem(5, MMFunctions.BundledArrows());
        i.setItem(6, MMFunctions.GoldenHead());

        p.openInventory(i);
        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(),  "Recipes Menu"));
    }

    public static ItemStack DummyHead() {
        // Create dummy head for recipe
        ItemStack dummyHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) dummyHead.getItemMeta();

        // Name the head after the player and style it
        TextComponent head_name = Component.text("Player Head", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Speed II (10s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Saturation I (0.5s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("used to craft ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Golden Heads").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(".").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build()));

        meta.customName(head_name);
        dummyHead.setItemMeta(meta);
        return dummyHead;
    }

    public static Player CurrentTarget(Player hunter) {
        List<MetadataValue> metadata = hunter.getMetadata("TargetedPlayer");
        return ManhuntCommand.runnerArray.get(metadata.getFirst().asInt());
    }

    public static ItemStack HunterCompass(Player hunter) {

        // Create compass ItemStack
        ItemStack hunterCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) hunterCompass.getItemMeta();

        // Now give it the custom item properties
        TextComponent compass_name = Component.text("Hunter's Compass",  NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CURRENT TARGET: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(CurrentTarget(hunter).getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("A compass that points at the runner(s).").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Right click to refresh the location").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("and left click to change target.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(compass_name);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.HUNTER_COMPASS, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Set the item meta
        hunterCompass.setItemMeta(meta);
        return hunterCompass;
    }

    public static void GenerateEndPlatform(World world) {
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

    public static void EnterSpectator (Player p) {
            p.setGameMode(GameMode.ADVENTURE);
            p.setAllowFlight(true);
            // Hide the spectator from all players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasMetadata("DeadRunner"))
                    onlinePlayer.hidePlayer(ModernManhunt.getInstance(), p);
            }
        }

    public static ItemStack SpectatorCompass() {

        // Create compass ItemStack
        ItemStack spectatorCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) spectatorCompass.getItemMeta();

        // Now give it the custom item properties
        TextComponent compass_name = Component.text("Spectator's Compass",  NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("USE").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text( " to open a menu and choose").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("the player you wish to spectate.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(compass_name);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.SPECTATOR_COMPASS, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Set the item meta
        spectatorCompass.setItemMeta(meta);
        return spectatorCompass;
    }

    public static ItemStack getSpecHead(Player p) {

        // Create the player head
        ItemStack specHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) specHead.getItemMeta();

        // Name the head after the player and style it
        TextComponent head_name = Component.text("");

        if (ManhuntCommand.mmHunters.getEntries().contains(p.getName())) {
            head_name = Component.text(p.getName(), NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
            meta.lore(List.of(Component.text("Hunter").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true)));
        } else {
            head_name = Component.text(p.getName(), NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
            meta.lore(List.of(Component.text("Runner").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true)));
        }

        meta.customName(head_name);
        meta.setOwningPlayer(p);

        // Set the item meta and return it
        specHead.setItemMeta(meta);
        return specHead;
    }

    public static void ExitSpectator(Player p) {
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                onlinePlayers.showPlayer(ModernManhunt.getInstance(), p);
            }
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

    public static ItemStack TPStar() {

        // Create compass ItemStack
        ItemStack tpStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = tpStar.getItemMeta();

        // Now give it the custom item properties
        TextComponent star_name = Component.text("Teleportation Star",  NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("USE").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text( " to open a menu and choose the").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("hunter you wish to teleport to.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(star_name);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.TP_STAR, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Set the item meta
        tpStar.setItemMeta(meta);
        return tpStar;
    }

    }
