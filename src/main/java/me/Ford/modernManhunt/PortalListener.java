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
        Location to;

        // Check if traveling from Nether to Overworld
        if (fromWorld.getEnvironment() == World.Environment.NETHER && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            String name = fromWorld.getName();
            toWorld = Bukkit.getWorld(name.substring(0, name.length() - 7)); // Overworld name
            if (toWorld == null) return;

            // 1:1 coordinate mapping
            to = new Location(toWorld, from.getX(), from.getY(), from.getZ());

        } else if (fromWorld.getEnvironment() == World.Environment.NORMAL && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
            toWorld = Bukkit.getWorld(fromWorld.getName() + "_nether"); // Nether name
            if (toWorld == null) return;

            // 1:1 coordinate mapping
            to = new Location(toWorld, from.getX(), from.getY(), from.getZ());
        } else {
            return; // Ignore other dimensions
        }

        // Set the destination manually
        event.setTo(to);
    }
}
