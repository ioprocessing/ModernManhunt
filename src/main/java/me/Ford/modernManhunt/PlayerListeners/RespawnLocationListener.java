package me.Ford.modernManhunt.PlayerListeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnLocationListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        String currentWorld = p.getLocation().getWorld().getName();
        if (currentWorld.endsWith("_the_end")) {
            currentWorld = currentWorld.substring(0, currentWorld.length() - 8);
        } else if (currentWorld.endsWith("_nether")) {
            currentWorld = currentWorld.substring(0, currentWorld.length() - 7);
        }

        World targetWorld = Bukkit.getWorld(currentWorld);
        if (targetWorld == null) return;

        Location targetSpawn = targetWorld.getSpawnLocation();

        // If bed is missing or respawn is in the wrong world, override
        if (e.isMissingRespawnBlock() || e.getRespawnLocation().getWorld() != targetWorld) {
            e.setRespawnLocation(targetSpawn);
            p.setRespawnLocation(targetSpawn, true); // Update personal spawn
        }
    }
}
