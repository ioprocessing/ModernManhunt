package org.ioprocessing.modernManhunt.teams;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.items.SpectatorCompass;

public class Spectator implements Listener {
    public static final NamespacedKey SPECTATING = new NamespacedKey(Core.getInstance(), "Spectating");

    public static void startSpectating(Player player) {
        Core.getInstance().getManhuntManager().resetPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        setSpectating(player, true);
        player.setInvisible(true);
        player.setAllowFlight(true);

        player.give(SpectatorCompass.getSpectatorCompass());
        player.updateInventory();
        Core.getInstance().getTeamManager().setSpectatorTeam(player);
    }

    public static void stopSpectating(Player player) {
        clearSpectating(player);

        Core.getInstance().getTeamManager().removeSpectator(player);
        Core.getInstance().getManhuntManager().resetPlayer(player);
    }

    public static Boolean isSpectating(Player player) {
        if(player.getPersistentDataContainer().has(SPECTATING, PersistentDataType.BOOLEAN))
            return player.getPersistentDataContainer().get(SPECTATING, PersistentDataType.BOOLEAN);
        return false;
    }
    public static void setSpectating(Player player, boolean value) {
        player.getPersistentDataContainer().set(SPECTATING, PersistentDataType.BOOLEAN, value);
    }

    public static void clearSpectating(Player player) {
        player.getPersistentDataContainer().remove(SPECTATING);
    }

    /**
     * Stop spectators from interacting with the world
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // If the player is a spectator that left, re-apply vanish and flight
        if (isSpectating(player)) {
            player.setInvisible(true);
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        // Cancel all damage dealt to spectators
        if (!(event.getEntity() instanceof Player player))
            return;
        if (isSpectating(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Cancel all damage dealt by spectators
        if (!(event.getDamager() instanceof Player player))
            return;
        if (isSpectating(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        // Cancel all item pick-up events from spectators
        if (!(event.getEntity() instanceof Player player))
            return;
        if (isSpectating(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // Cancel all interactions of entities from spectators (like villager trading)
        if (isSpectating(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onSpecInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // If the player is a spectator, cancel the interaction
        if (isSpectating(player)) {
            event.setCancelled(true);
        }
    }
}
