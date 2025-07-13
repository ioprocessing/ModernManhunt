package me.Ford.modernManhunt;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerHeads {
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
}
