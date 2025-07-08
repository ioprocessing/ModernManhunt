package me.Ford.modernManhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListener implements Listener {
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location from = event.getFrom();
        World fromWorld = from.getWorld();

        if (fromWorld == null) return;

        World toWorld;
        Location to = null;

        // Check if traveling from Nether to Overworld
        if (fromWorld.getEnvironment() == World.Environment.NETHER && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            String name = fromWorld.getName();
            toWorld = Bukkit.getWorld(name.substring(0, name.length() - 7)); // Overworld name
            if (toWorld == null) return;

            // 1:1 coordinate mapping
            to = new Location(toWorld, from.getX(), from.getY(), from.getZ());

        } else if (fromWorld.getEnvironment() == World.Environment.NORMAL) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                // If we go to the Nether from the Overworld:
                toWorld = Bukkit.getWorld(fromWorld.getName() + "_nether"); // Nether name
                if (toWorld == null) return;
                // 1:1 coordinate mapping
                to = new Location(toWorld, from.getX(), from.getY(), from.getZ());
            } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                // If we go to the End from the Overworld:
                toWorld = Bukkit.getWorld(fromWorld.getName() + "_the_end"); // The End name
                if (toWorld == null) return;
                // Always send them to the obsidian platform
                to = new Location(toWorld, 100.5, 49, 0.5, 90.0f, event.getPlayer().getPitch());
            }
        } else if (fromWorld.getEnvironment() == World.Environment.THE_END && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL){
            // If we go to the Overworld from the End:
            String name = fromWorld.getName();
            toWorld = Bukkit.getWorld(name.substring(0, name.length() - 7));
            if (toWorld == null) return;
            // Always send them to their respawn location
            to = event.getPlayer().getRespawnLocation();
        } else return;

        // Set the destination manually
        if (to != null) {
            event.setTo(to);
        }
    }
}
