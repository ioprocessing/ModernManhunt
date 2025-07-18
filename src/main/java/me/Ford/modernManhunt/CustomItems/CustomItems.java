package me.Ford.modernManhunt.CustomItems;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
                        .append(Component.text(" to gain Speed II (10s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Saturation I (0.25s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
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

    public static ItemStack goldenHead() {

        // Create golden head ItemStack
        ItemStack goldenHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) goldenHead.getItemMeta();

        // Now give it the custom item properties
        TextComponent head_name = Component.text("Golden Head", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Saturation I (0.5s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("Speed II (15s), Absorption II (120s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
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
        TextComponent potion_name;
        if (splash)
            potion_name = Component.text("Splash Potion of Fire Resistance",  NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        else
            potion_name = Component.text("Potion of Fire Resistance",  NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0), true);
        meta.setColor(Color.fromRGB(255, 153, 0));
        meta.customName(potion_name);
        potion.setItemMeta(meta);
        return potion;
    }
}
