package me.Ford.modernManhunt;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bartering {
    public static ItemStack getCustomDrop() {
        final int random = MMFunctions.random(423, 1);
        // If else tree to calculate random barter using 1.16.1 loot table
        if ((1 <= random) && (random <= 5)) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = book.getItemMeta();
            meta.addEnchant(Enchantment.SOUL_SPEED, MMFunctions.random(3,1), false);
            book.setItemMeta(meta);
            return book;
        } else if ((6 <= random) && (random <= 13)) {
            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            ItemMeta meta = boots.getItemMeta();
            meta.addEnchant(Enchantment.SOUL_SPEED, MMFunctions.random(3,1), false);
            boots.setItemMeta(meta);
            return boots;
        } else if ((14 <= random) && (random <= 23)) {
            return new ItemStack(Material.IRON_NUGGET, MMFunctions.random(36,9));
        } else if ((24 <= random) && (random <= 33)) {
            ItemStack potion = new ItemStack(Material.SPLASH_POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            PotionEffect effect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0);
            meta.addCustomEffect(effect, true);
            meta.setColor(Color.fromRGB(228, 154, 58));
            potion.setItemMeta(meta);
            return potion;
        } else if ((34 <= random) && (random <= 43)) {
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            PotionEffect effect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0);
            meta.addCustomEffect(effect, true);
            meta.setColor(Color.fromRGB(228, 154, 58));
            potion.setItemMeta(meta);
            return potion;
        } else if ((44 <= random) && (random <= 63)) {
            return new ItemStack(Material.QUARTZ, MMFunctions.random(16,8));
        } else if ((64 <= random) && (random <= 83)) {
            return new ItemStack(Material.GLOWSTONE_DUST, MMFunctions.random(12,5));
        } else if ((84 <= random) && (random <= 103)) {
            return new ItemStack(Material.MAGMA_CREAM, MMFunctions.random(6,2));
        } else if ((104 <= random) && (random <= 123)) {
            return new ItemStack(Material.ENDER_PEARL, MMFunctions.random(8,4));
        } else if ((124 <= random) && (random <= 143)) {
            return new ItemStack(Material.STRING, MMFunctions.random(24,8));
        } else if ((144 <= random) && (random <= 183)) {
            return new ItemStack(Material.FIRE_CHARGE, MMFunctions.random(5,1));
        } else if ((184 <= random) && (random <= 223)) {
            return new ItemStack(Material.GRAVEL, MMFunctions.random(16,8));
        } else if ((224 <= random) && (random <= 263)) {
            return new ItemStack(Material.LEATHER, MMFunctions.random(10,4));
        } else if ((264 <= random) && (random <= 303)) {
            return new ItemStack(Material.NETHER_BRICK, MMFunctions.random(16,4));
        } else if ((304 <= random) && (random <= 343)) {
            return new ItemStack(Material.OBSIDIAN, 1);
        } else if ((344 <= random) && (random <= 383)) {
            return new ItemStack(Material.CRYING_OBSIDIAN, MMFunctions.random(3,1));
        } else {
            return new ItemStack(Material.SOUL_SAND, MMFunctions.random(16,4));
        }
    }
}
