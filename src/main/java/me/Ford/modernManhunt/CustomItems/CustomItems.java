package me.Ford.modernManhunt.CustomItems;

import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.StructureSearchResult;

import java.util.Arrays;
import java.util.List;

public class CustomItems {
    public static ItemStack consumablePlayerHead(Player p) {

        // Create the player head
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        // Name the head after the player and style it
        TextComponent head_name = Component.text(p.getName() + "'s Head", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Speed I (15s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Regeneration II (5s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
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
        cd.setCooldownGroup(Keys.PLAYER_HEAD_COOLDOWN);
        meta.setUseCooldown(cd);

        // Set the item meta and add it to the loot dropped
        playerHead.setItemMeta(meta);
        return playerHead;
    }

    public static ItemStack goldenHead() {

        // Create golden head ItemStack
        ItemStack goldenHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) goldenHead.getItemMeta();

        // Now give it the custom item properties
        TextComponent head_name = Component.text("Golden Head", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Regeneration III (5s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("Speed II (15s), and Absorption I (120s).").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.customName(head_name);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("golden_head"));
        meta.setCustomModelDataComponent(model);

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.GOLDEN_HEAD, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(Keys.GOLDEN_HEAD_COOLDOWN);
        meta.setUseCooldown(cd);

        // Set the item meta
        goldenHead.setItemMeta(meta);

        return goldenHead;
    }

    public static ItemStack primedPickaxe() {

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

    public static ItemStack strengthenedSword() {

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

    public static ItemStack bolsteredBow() {

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

    public static ItemStack bundledArrows() {
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

    public static ItemStack compactAnvil() {
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

    public static ItemStack dummyHead() {
        // Create dummy head for recipe
        ItemStack dummyHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) dummyHead.getItemMeta();

        // Name the head after the player and style it
        TextComponent head_name = Component.text("Player Head", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Speed I (15s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Regeneration II (5s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("used to craft ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Golden Heads").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(".").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build()));

        meta.customName(head_name);
        dummyHead.setItemMeta(meta);
        return dummyHead;
    }

    public static ItemStack hunterCompass(Player hunter) {

        // Create compass ItemStack
        ItemStack hunterCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) hunterCompass.getItemMeta();

        // Now give it the custom item properties
        TextComponent compass_name = Component.text("Hunter's Compass",  NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CURRENT TARGET: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(Functions.currentTarget(hunter).getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
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

    public static ItemStack spectatorCompass() {

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

        if (Functions.mmHunters.getEntries().contains(p.getName())) {
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

    public static ItemStack tpStar() {

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

        // Add teleporting cooldown
        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(Keys.TP_COOLDOWN);
        meta.setUseCooldown(cd);

        // Set the item meta
        tpStar.setItemMeta(meta);
        return tpStar;
    }

    public static ItemStack loyaltyBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta esm = (EnchantmentStorageMeta)book.getItemMeta();
        esm.addStoredEnchant(Enchantment.LOYALTY, 1, false);
        book.setItemMeta(esm);
        return book;
    }

    public static ItemStack fireResistancePotion(boolean splash) {
        ItemStack potion = new ItemStack(splash ? Material.SPLASH_POTION : Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.FIRE_RESISTANCE);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack swiftnessPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.SWIFTNESS);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack strengthPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.STRENGTH);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack ominousBottle(int level) {
        ItemStack bottle = new ItemStack(Material.OMINOUS_BOTTLE);
        OminousBottleMeta meta = (OminousBottleMeta) bottle.getItemMeta();
        meta.setAmplifier(level);
        bottle.setItemMeta(meta);
        return bottle;
    }

    public static ItemStack waterBottle() {
        ItemStack bottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        meta.setBasePotionType(PotionType.WATER);
        bottle.setItemMeta(meta);
        return bottle;
    }

    public static ItemStack dummyTrialMap() {
        // Create map ItemStack
        ItemStack trialMap = new ItemStack(Material.FILLED_MAP);

        // Set dummy map details
        MapMeta mapMeta = (MapMeta) trialMap.getItemMeta();

        // Set the color
        mapMeta.setColor(Color.fromRGB(194, 107, 76));
        TextComponent map_name = Component.text("Trial Explorer Map", TextColor.color(206, 112, 43)).decoration(TextDecoration.ITALIC, false);
        mapMeta.customName(map_name);

        mapMeta.lore(Arrays.asList(Component.text("A mysterious map that leads").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("to the nearest Trial Chambers.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));


        // Add custom tag that we'll check for later
        mapMeta.getPersistentDataContainer().set(Keys.DUMMY_MAP, PersistentDataType.BOOLEAN, true);

        trialMap.setItemMeta(mapMeta);

        return trialMap;
    }

    public static ItemStack trialChamberMap(Player p) {
        World bukkitWorld = p.getWorld();
        ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();

        // Locate nearest Trial Chambers structure
        StructureSearchResult result = bukkitWorld.locateNearestStructure(
                p.getLocation(),
                Structure.TRIAL_CHAMBERS,
                10_000,
                false
        );
        if (result == null) {
            p.sendMessage(Component.text("No Trial Chambers found nearby."));
            return null;
        }
        Location loc = result.getLocation();

        // Create explorer map via NMS
        net.minecraft.world.item.ItemStack nmsMap = MapItem.create(
                nmsWorld,
                loc.getBlockX(),
                loc.getBlockZ(),
                (byte) 2,
                true,
                true
        );

        MapItemSavedData mapData = MapItem.getSavedData(nmsMap, nmsWorld);
        if (mapData == null) {
            mapData = MapItemSavedData.createFresh(
                    loc.getBlockX(),
                    loc.getBlockZ(),
                    (byte) 2,
                    true,
                    true,
                    nmsWorld.dimension()
            );
            MapId mapId = nmsMap.get(DataComponents.MAP_ID);
            nmsWorld.setMapData(mapId, mapData);
        }

        // Render water as orange outlines
        MapItem.renderBiomePreviewMap(nmsWorld, nmsMap);

        // Add the Trial Chambers decoration
        Holder<MapDecorationType> decorationType = MapDecorationTypes.TRIAL_CHAMBERS;
        MapItemSavedData.addTargetDecoration(
                nmsMap,
                new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                "Trial Chambers",
                decorationType
        );

        // Convert to Bukkit ItemStack and set display name
        ItemStack trialMap = CraftItemStack.asBukkitCopy(nmsMap);

        // Style the map like the dummy map
        MapMeta meta = (MapMeta) trialMap.getItemMeta();
        ItemMeta dummyMeta = dummyTrialMap().getItemMeta();
        List<Component> dummyLore = dummyMeta.lore();
        meta.lore(dummyLore);
        meta.customName(dummyMeta.customName());

        // Add custom tag that we'll check for later
        meta.getPersistentDataContainer().set(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().remove(Keys.DUMMY_MAP);

        trialMap.setItemMeta(meta);

        return trialMap;
    }
}
