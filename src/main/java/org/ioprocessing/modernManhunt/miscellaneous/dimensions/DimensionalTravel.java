package org.ioprocessing.modernManhunt.miscellaneous.dimensions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.teams.Spectator;

public class DimensionalTravel implements Listener {
    private final ConfigManager<CoreConfig> coreConfig;

    public DimensionalTravel(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    @EventHandler
    public void onPlayerDimensionChange(PlayerPortalEvent event) {
        CoreConfig.DimensionalTravelAdjustments dimensionalTravel = this.coreConfig.getConfig().getDimensionalTravelAdjustments();
        int travelRatio = dimensionalTravel.isEnabled() ? dimensionalTravel.getOverworldToNetherDistanceRatio() : 8;

        Location from = event.getFrom();
        World fromWorld = from.getWorld();
        if (fromWorld == null) return;

        // Only handle manhunt dimensions
        if (!fromWorld.getKey().getNamespace().equals(Core.MANHUNT_NAMESPACE)) return;

        World toWorld;
        Location to = null;

        if (fromWorld.getEnvironment() == World.Environment.NETHER
                && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            toWorld = Bukkit.getWorld(Core.MANHUNT_OVERWORLD);
            if (toWorld == null) return;
            to = new Location(toWorld, from.getX() * travelRatio, from.getY(), from.getZ() * travelRatio);

        } else if (fromWorld.getEnvironment() == World.Environment.NORMAL) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                toWorld = Bukkit.getWorld(Core.MANHUNT_NETHER);
                if (toWorld == null) return;
                to = new Location(toWorld, from.getX() / travelRatio, from.getY(), from.getZ() / travelRatio);

            } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                toWorld = Bukkit.getWorld(Core.MANHUNT_THE_END);
                if (toWorld == null) return;
                generateEndPlatform(toWorld);
                to = new Location(toWorld, 100.5, 49, 0.5, 90.0f, event.getPlayer().getPitch());
            }

        } else if (fromWorld.getEnvironment() == World.Environment.THE_END
                && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            toWorld = Bukkit.getWorld(Core.MANHUNT_OVERWORLD);
            if (toWorld == null) return;
            to = event.getPlayer().getRespawnLocation();

        } else return;

        if (to != null) {
            event.setTo(to);
        }
    }

    @EventHandler
    public void onSpectatorDimensionChange(PlayerTeleportEvent event) {
        // If a spectator is changing dimensions, maintain flight
        if(!event.getFrom().getWorld().equals(event.getTo().getWorld())
                && Spectator.isSpectating(event.getPlayer())) {
            // Tick delay required to maintain flight across dimensions
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> event.getPlayer().setAllowFlight(true), 1L);
        }
    }

    private void generateEndPlatform(World world) {
        // Clear area for obsidian platform
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block block = world.getBlockAt(x + 98, 48, z - 2);
                if (block.getType() == Material.OBSIDIAN) continue;
                block.breakNaturally(false, true);
                block.setType(Material.OBSIDIAN);
            }
        }

        // Clear area for player
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int y = 0; y < 3; y++) {
                    world.getBlockAt(x + 98, 49 + y, z - 2).breakNaturally(false, true);
                }
            }
        }
    }
}
