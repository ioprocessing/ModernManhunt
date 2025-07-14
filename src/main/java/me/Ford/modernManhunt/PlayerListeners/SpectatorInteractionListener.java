package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.GUI.TeleporterGUI;
import me.Ford.modernManhunt.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpectatorInteractionListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // If the player is a spectator, re-apply vanish and flight
        if (p.hasMetadata("DeadRunner")) {
            Functions.enterSpectator(p);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        // Cancel all damage dealt to spectators
        if (!(e.getEntity() instanceof Player))
            return;
        if (e.getEntity().hasMetadata("DeadRunner"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        // Cancel all damage dealt by spectators
        if (e.getDamager().hasMetadata("DeadRunner"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        // Cancel all item pick-up events from spectators
        if (!(e.getEntity() instanceof Player))
            return;
        if (e.getEntity().hasMetadata("DeadRunner"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSpecInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        // If the player is a spectator, cancel the interaction
        if (p.hasMetadata("DeadRunner")) {
            e.setCancelled(true);
            // If they right-click with the spectator compass, open the spectator menu
            if (e.getItem() != null && e.getAction().isRightClick() && e.getItem().getItemMeta().getPersistentDataContainer().has(Keys.SPECTATOR_COMPASS))
                TeleporterGUI.openSpectatorGUI(p);
        }
    }
}
