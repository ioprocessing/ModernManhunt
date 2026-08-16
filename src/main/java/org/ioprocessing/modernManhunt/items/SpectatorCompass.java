package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.menus.SpectatorGui;
import org.ioprocessing.modernManhunt.teams.Spectator;

import java.util.Arrays;

public class SpectatorCompass implements Listener {
    public static final NamespacedKey SPECTATOR_COMPASS_KEY = new NamespacedKey(Core.getInstance(), "SpectatorCompass");

    /**
     * @return Spectator Compass custom item
     */
    public static ItemStack getSpectatorCompass() {
        ItemStack spectatorCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) spectatorCompass.getItemMeta();

        // Custom item properties
        TextComponent compassName = Component.text("Spectator's Compass").decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("USE").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text( " to open a menu and choose").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("the player you wish to spectate").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        meta.getPersistentDataContainer().set(SPECTATOR_COMPASS_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.PREVENT_ITEM_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        spectatorCompass.setItemMeta(meta);
        spectatorCompass.setData(DataComponentTypes.ITEM_NAME, compassName);
        spectatorCompass.setData(DataComponentTypes.RARITY, ItemRarity.RARE);

        return spectatorCompass;
    }

    /**
     * Open spectator compass GUI when player right-clicks with the compass
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onSpecCompassUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        // If they right-click with the spectator compass, open the spectator menu
        if (event.getAction().isRightClick() && CustomItems.hasKey(item, SpectatorCompass.SPECTATOR_COMPASS_KEY)) {
            event.setCancelled(true);
            new SpectatorGui(player).open();
        }
    }

    /**
     * If a player is a spectator when they respawn, give them a spectator compass
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH)
            return;
        Player player = event.getPlayer();
        if(Spectator.isSpectating(player)) {
            player.give(getSpectatorCompass());
        }
    }
}
